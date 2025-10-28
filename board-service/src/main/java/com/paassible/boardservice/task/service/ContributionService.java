package com.paassible.boardservice.task.service;

import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.client.PositionClient;
import com.paassible.boardservice.client.UserClient;
import com.paassible.boardservice.task.dto.ContributionResponse;
import com.paassible.boardservice.task.entity.Task;
import com.paassible.boardservice.task.entity.enums.TaskStatus;
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

    public List<ContributionResponse> getContributions(Long boardId) {

        List<BoardMember> members = boardMemberService.getBoardMembersByBoard(boardId);
        List<ContributionResponse> responses = new ArrayList<>();

        for (BoardMember member : members) {
            ContributionResponse response = getContributionForMember(member.getUserId(), member.getBoardId(), member.getPositionId());
            responses.add(response);
        }

        return responses;
    }


    public ContributionResponse getContributionForMember(Long userId, Long boardId, Long positionId) {

        int totalCommunication = 100;

        // 업무 완료율 계산
        List<Task> tasks = taskService.getAssigneeTasks(userId, boardId);
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
        double taskCompletion = tasks.isEmpty() ? 0 : (completed * 100.0 / tasks.size());

        // 회의 참석률 (추후 로직 변경 가능)
        double attendanceRate = 50;

        // 커뮤니케이션 횟수 (임시 값)
        int commValue = 20;

        // 최종 기여도 계산
        double contribution =
                (taskCompletion * 0.5) +
                        (attendanceRate * 0.3) +
                        ((commValue / (double) totalCommunication) * 100 * 0.2);

        // 응답 객체 생성
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

