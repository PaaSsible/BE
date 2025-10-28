package com.paassible.boardservice.portfolio.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.board.service.BoardService;
import com.paassible.boardservice.board.service.ContributionService;
import com.paassible.boardservice.client.PositionClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.portfolio.dto.PortfolioAiRequest;
import com.paassible.boardservice.portfolio.dto.PortfolioAiResponse;
import com.paassible.boardservice.portfolio.dto.PortfolioTemplate;
import com.paassible.boardservice.board.dto.ContributionResponse;
import com.paassible.boardservice.task.entity.Task;
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

    public PortfolioAiResponse generatePortfolioByAi(Long userId, Long boardId) {

        String prompt = generatePromptTemplate(userId, boardId);

        GenerateContentResponse response = client.models
                .generateContent("gemini-2.5-flash", prompt, config);

        PortfolioAiRequest request = createPortfolio(userId, boardId, response);
        userClient.generatePortfolioAi(request);

        String positionName = positionClient.getPositionName(request.getPositionId());
        return PortfolioAiResponse.from(request, positionName);
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

        ContributionResponse response = contributionService.getContributionForMember(userId, boardId, positionId);

        String taskCompletion = String.valueOf(response.getTaskCompletion());
        String attendanceRate = String.valueOf(response.getAttendanceRate());
        String value = String.valueOf(response.getCommunicationFrequency().getValue());
        String total = String.valueOf(response.getCommunicationFrequency().getTotal());
        String contribution = String.valueOf(response.getContribution());

        return portfolioTemplate.buildPrompt(projectName, position, features, taskCompletion, attendanceRate, value, total, contribution);
    }
}
