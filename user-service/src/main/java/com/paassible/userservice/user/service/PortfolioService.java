package com.paassible.userservice.user.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.userservice.client.PositionClient;
import com.paassible.userservice.user.dto.*;
import com.paassible.userservice.user.entity.Portfolio;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.entity.enums.MainCategory;
import com.paassible.userservice.user.entity.enums.SubCategory;
import com.paassible.userservice.user.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createPortfolioAi(PortfolioAiRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .userId(request.getUserId())
                .positionId(request.getPositionId())
                .title(request.getTitle())
                .summary(request.getSummary())
                .description(request.getDescription())
                .mainCategory(MainCategory.valueOf(request.getMainCategory()))
                .subCategory(SubCategory.valueOf(request.getSubCategory()))
                .contribution(request.getContribution())
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
    public PortfolioDetailResponse getPortfolioDetail(Long portfolioId) {
        Portfolio portfolio = getPortfolio(portfolioId);

        String positionName = positionClient.getPositionName(portfolio.getPositionId());
        return PortfolioDetailResponse.from(portfolio, positionName);
    }

    @Transactional(readOnly = true)
    public PortfolioPageResponse getPortfoliosByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PortfolioResponse> portfolioResponses = portfolioRepository.findByUserId(userId, pageable)
                .map(portfolio -> {
                    String positionName = positionClient.getPositionName(portfolio.getPositionId());
                    return PortfolioResponse.from(portfolio, positionName);
                });

        return PortfolioPageResponse.from(portfolioResponses);
    }

    public void validatePortfolioOwner(Long userId, Portfolio portfolio) {
        if (!portfolio.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_NOT_OWNER);
        }
    }
}
