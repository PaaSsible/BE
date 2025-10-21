package com.paassible.userservice.user.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.userservice.client.PositionClient;
import com.paassible.userservice.user.dto.PortfolioRequest;
import com.paassible.userservice.user.dto.PortfolioResponse;
import com.paassible.userservice.user.entity.Portfolio;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    private final PositionClient positionClient;

    public Portfolio getPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));
    }

    @Transactional
    public void createPortfolio(Long userId, PortfolioRequest request) {
        User user = userService.getUser(userId);

        Portfolio portfolio = Portfolio.builder()
                .userId(user.getId())
                .positionId(request.getPositionId())
                .title(request.getTitle())
                .summary(request.getSummary())
                .description(request.getDescription())
                .build();

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void updatePortfolio(Long userId, Long portfolioId, PortfolioRequest request) {
        Portfolio portfolio = getPortfolio(portfolioId);
        validatePortfolioOwner(userId, portfolio);

        portfolio.updateInfo(request.getPositionId(), request.getTitle(), request.getSummary(), request.getDescription());
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = getPortfolio(portfolioId);
        validatePortfolioOwner(userId, portfolio);

        portfolioRepository.deleteById(portfolioId);
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioDetail(Long portfolioId) {
        Portfolio portfolio = getPortfolio(portfolioId);

        String positionName = positionClient.getPositionName(portfolio.getPositionId());
        return PortfolioResponse.from(portfolio, positionName);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByUser(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .stream()
                .map(portfolio -> {
                    String positionName = positionClient.getPositionName(portfolio.getPositionId());
                    return PortfolioResponse.from(portfolio, positionName);
                })
                .toList();
    }

    public void validatePortfolioOwner(Long userId, Portfolio portfolio) {
        if (!portfolio.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_NOT_OWNER);
        }
    }
}
