package com.cyes.webserver.domain.quiz.service;

import com.cyes.webserver.domain.member.entity.Member;
import com.cyes.webserver.domain.member.repository.MemberRepository;
import com.cyes.webserver.domain.quiz.dto.GroupQuizInfoResponse;
import com.cyes.webserver.domain.quiz.dto.QuizCreateRequestToServiceDto;
import com.cyes.webserver.domain.quiz.dto.QuizCreateResponse;
import com.cyes.webserver.domain.quiz.dto.QuizInfoResponse;
import com.cyes.webserver.domain.quiz.entity.Quiz;
import com.cyes.webserver.domain.quiz.repository.QuizRepository;
import com.cyes.webserver.domain.quizproblem.repository.QuizProblemRepository;
import com.cyes.webserver.domain.quizproblem.service.QuizProblemService;
import com.cyes.webserver.exception.CustomException;
import com.cyes.webserver.exception.CustomExceptionList;
import com.cyes.webserver.redis.service.ScheduleReserveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizProblemRepository quizProblemRepository;
    private final MemberRepository memberRepository;
    private final ScheduleReserveService scheduleReserveService;
    private final QuizProblemService quizProblemService;


    /* 
    퀴즈 정보 조회
     */
    public QuizInfoResponse searchQuiz(LocalDateTime now) {

        // 가장 최근에 생성된 라이브 퀴즈쇼 조회
        Quiz quiz = quizRepository.findLiveQuiz(now).orElse(null);

        QuizInfoResponse quizInfoResponse;

        if (quiz == null) {
            quizInfoResponse = QuizInfoResponse.builder()
                    .quizId(-1L)
                    .quizTitle("")
                    .quizStartDate(LocalDateTime.now())
                    .build();
        } else {
            quizInfoResponse = quiz.toQuizInfoResponse();
        }
        // Entity -> Dto

        return quizInfoResponse;
    }

    public List<GroupQuizInfoResponse> searchGroupQuiz(LocalDateTime now) {

        // 퀴즈를 만든 사람이 일반 유저인 퀴즈 조회
        List<Quiz> quizList = quizRepository.findGroupQuiz(now).orElseThrow(() -> new CustomException(CustomExceptionList.QUIZ_NOT_FOUND_ERROR));

        // Entirty -> Dto
        List<GroupQuizInfoResponse> list = new ArrayList<>();
        for(Quiz quiz : quizList) {
            list.add(quiz.toGroupQuizInfoResponse());
        }

        return list;

    }
    /*
    퀴즈 개설
     */
    public QuizCreateResponse createQuiz(QuizCreateRequestToServiceDto dto) throws JsonProcessingException {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new CustomException(CustomExceptionList.MEMBER_NOT_FOUND_ERROR));

        // Dto -> Entity
        Quiz quiz = dto.toQuizEntity(member);

        // Insert Quiz
        quizRepository.save(quiz);

        //Insert QuizProblem
        quizProblemService.createQuizProblemByQuiz(quiz, dto.getProblemList());


        scheduleReserveService.saveQuiz(quiz.getId(), quiz.getStartDateTime());

        // Entity -> Response Dto
        return quiz.toQuizCreateResponse();
    }
}
