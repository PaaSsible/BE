package com.paassible.userservice.user.service;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;
import com.paassible.userservice.file.service.ObjectStorageService;
import com.paassible.userservice.user.dto.PortfolioRequest;
import com.paassible.userservice.user.dto.PortfolioResponse;
import com.paassible.userservice.user.entity.Portfolio;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    private final ObjectStorageService fileStorageService;

    @Transactional
    public void createPortfolio(Long userId, PortfolioRequest request, MultipartFile file) {
        User user = userService.getUser(userId);

        String fileUrl = saveFile(file);

        Portfolio portfolio = Portfolio.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .fileUrl(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .build();

        portfolioRepository.save(portfolio);
    }

    @Transactional
    public void updatePortfolio(Long userId, Long portfolioId, PortfolioRequest request, MultipartFile file) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        if (!portfolio.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_NOT_OWNER);
        }

        portfolio.updateInfo(request.getTitle(), request.getDescription());

        if (file != null && !file.isEmpty()) {
            String fileUrl = saveFile(file);
            portfolio.updateFile(fileUrl, file.getOriginalFilename(), file.getContentType());
        }
    }

    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        if (!portfolio.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.PORTFOLIO_NOT_OWNER);
        }

        portfolioRepository.deleteById(portfolioId);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByUser(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .stream()
                .map(PortfolioResponse::from)
                .toList();
    }

    private String saveFile(MultipartFile file) {
        return fileStorageService.upload("portfolio", file);
    }
}
