package co.kr.capstonemju.JobBrief.domain.scrap.service;

import co.kr.capstonemju.JobBrief.domain.member.model.Member;
import co.kr.capstonemju.JobBrief.domain.news.controller.dto.NewsDto;
import co.kr.capstonemju.JobBrief.domain.news.controller.dto.NewsListDto;
import co.kr.capstonemju.JobBrief.domain.news.model.News;
import co.kr.capstonemju.JobBrief.domain.news.repository.NewsRepository;
import co.kr.capstonemju.JobBrief.domain.scrap.controller.dto.ScrapCreateRequest;
import co.kr.capstonemju.JobBrief.domain.scrap.model.Scrap;
import co.kr.capstonemju.JobBrief.domain.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final NewsRepository newsRepository;
    private static final int PAGE_SIZE = 10;


    public void saveScrap(Member member, Long id, ScrapCreateRequest scrapCreateRequest ) {
        News news =newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News Not Found"));
        Scrap scrapCheck = scrapRepository.findByNewsAndMember(news,member);
        if (scrapCheck == null){
            //create scrap
            Scrap newScrap = Scrap.builder().news(news).member(member).opinion(scrapCreateRequest.getOpinion()).build();
            scrapRepository.save(newScrap);
        }else {
            //update scrap
            scrapCheck.updateScrap(scrapCreateRequest.getOpinion());
            scrapRepository.save(scrapCheck);
        }
    }

    public NewsListDto getAllScrap(Member member, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE);
        Page<Scrap> scrapPage = scrapRepository.findByMember(member, pageRequest);
        List<Scrap> scrapList = scrapPage.getContent();
        List<News> newsList = new ArrayList<>();

        for (Scrap scrap : scrapList) {
            newsList.add(scrap.getNews());
        }
        List<NewsDto> newsDtoList = newsList.stream().map(NewsDto::new).toList();
        int totalPages = scrapPage.getTotalPages();

        return new NewsListDto(newsDtoList, page, totalPages);
    }


}
