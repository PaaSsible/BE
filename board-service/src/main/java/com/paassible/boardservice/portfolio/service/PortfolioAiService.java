package com.paassible.boardservice.portfolio.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.board.service.BoardService;
import com.paassible.boardservice.client.PositionClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.portfolio.dto.PortfolioAiRequest;
import com.paassible.boardservice.portfolio.dto.PortfolioTemplate;
import com.paassible.boardservice.task.dto.ContributionResponse;
import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.service.ContributionService;
import com.paassible.boardservice.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioAiService {

    private final Client client;
    private final GenerateContentConfig config;

    private final PortfolioTemplate portfolioTemplate;

    private final BoardService boardService;
    private final BoardMemberService  boardMemberService;
    private final ContributionService contributionService;

    private final UserClient userClient;
    private final PositionClient positionClient;

    private final TaskService taskService;

    public PortfolioAiRequest generatePortfolioByAi(Long userId, Long boardId) {

        String prompt = generatePromptTemplate(userId, boardId);

        GenerateContentResponse response = client.models
                .generateContent("gemini-2.5-flash", prompt, config);

        PortfolioAiRequest request = createPortfolio(userId, boardId, response);
        userClient.generatePortfolioAi(request);

        return request;

    }

    public PortfolioAiRequest createPortfolio(Long userId, Long boardId, GenerateContentResponse response) {
        Board board = boardService.getBoard(boardId);
        Long positionId = boardMemberService.getBoardMember(userId, boardId).getPositionId();
        ContributionResponse contribution = contributionService.getContributionForMember(userId, boardId, positionId);

        return PortfolioAiRequest.builder()
                .userId(userId)
                .positionId(positionId)
                .title(board.getName())
                .summary(board.getContent())
                .description(response.text())
                .mainCategory(board.getActivityType().name())
                .subCategory(board.getDetailType().name())
                .contribution(contribution.getContribution())
                .build();
    }

    public String generatePromptTemplate(Long userId, Long boardId) {

        String projectName = boardService.getBoard(boardId).getName();

        BoardMember boardMember = boardMemberService.getBoardMember(userId, boardId);
        Long positionId = boardMember.getPositionId();
        String position = positionClient.getPositionName(positionId);

        List<Task> assignedTasks = taskService.getAssigneeTasks(userId, boardId);
        List<PortfolioTemplate.FeatureData> features = assignedTasks.stream()
                .map(task -> new PortfolioTemplate.FeatureData(task.getTitle(), task.getDescription()))
                .toList();

        // 이것도 기여도에서 구성 요소니까
        String achievements ="- 회의 출석률: 95%\n" +
                "- 작업 처리율: 12개 중 11개 완료\n" +
                "- 최종 결과: 전국 대학생 공모전 제출";

        double contribution = contributionService.getContributionForMember(userId, boardId, positionId).getContribution();

        return portfolioTemplate.buildPrompt(projectName, position, features, achievements, String.valueOf(contribution));
    }
}
