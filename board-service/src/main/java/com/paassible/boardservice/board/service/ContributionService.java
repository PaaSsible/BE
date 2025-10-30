package com.paassible.boardservice.board.service;

import com.paassible.boardservice.board.dto.CommunicationResponse;
import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.client.ChatClient;
import com.paassible.boardservice.client.MeetClient;
import com.paassible.boardservice.client.PositionClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.board.dto.ContributionResponse;
import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.entity.enums.TaskStatus;
import com.paassible.boardservice.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final BoardMemberService boardMemberService;
    private final TaskService taskService;

    private final UserClient userClient;
    private final PositionClient positionClient;
    private final MeetClient meetClient;
    private final ChatClient chatClient;

    public List<ContributionResponse> getContributions(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(userId, boardId);

        List<BoardMember> members = boardMemberService.getBoardMembersByBoard(boardId);
        List<ContributionResponse> responses = new ArrayList<>();

        for (BoardMember member : members) {
            ContributionResponse response = getContributionForMember(member.getUserId(), member.getBoardId(), member.getPositionId());
            responses.add(response);
        }
        return responses;
    }

    public ContributionResponse getContributionForMember(Long userId, Long boardId, Long positionId) {

        List<Task> tasks = taskService.getAssigneeTasks(userId, boardId);
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
        double taskCompletion = tasks.isEmpty() ? 0 : (completed * 100.0 / tasks.size());

        System.out.println("meetClient");
        double attendanceRate = meetClient.getContribution(userId, boardId);

        System.out.println("chatClient");
        CommunicationResponse response = chatClient.getCommunicationFrequency(userId, boardId);
        long commValue = response.getValue();
        long totalCommunication = response.getTotal();

        double contribution =
                (taskCompletion * 0.5) +
                        (attendanceRate * 0.3) +
                        ((commValue / (double) totalCommunication) * 100 * 0.2);

        return ContributionResponse.builder()
                .id(userId)
                .memberName(userClient.getUser(userId).getNickname())
                .part(positionClient.getPositionName(positionId))
                .taskCompletion(Math.round(taskCompletion))
                .attendanceRate(Math.round(attendanceRate))
                .communicationFrequency(
                        new ContributionResponse.CommunicationFrequency(commValue, totalCommunication)
                )
                .contribution(Math.round(contribution))
                .build();
    }
}

