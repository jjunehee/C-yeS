package com.cyes.webserver.domain.quiz.controller;

import com.cyes.webserver.domain.quiz.dto.*;
import com.cyes.webserver.domain.quiz.service.QuizService;
import com.cyes.webserver.domain.stompSocket.dto.SubmitRedis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@Slf4j
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "퀴즈", description = "퀴즈에 관한 API")
public class QuizController {

    private final QuizService quizService;

    /*이거 참고해서 메서드 설명 넣어 주세용~~
     @Operation(summary = "객관식 문제등록", description = "객관식 문제를 등록하는 메서드이다.\n" +
             "question에 문제 내용, answer에 답안 내용\n" +
             "choices에 보기가 String 배열로 4개 들어가면 된다.\n"+
             "problemCategory에는 NETWORK, OS, DB, DATASTRUCTURE, ALGORITHM, DESIGNPATTERN, COMPUTERARCHITECTURE 중 하나가 문자열로 들어가야 한다.")*/
    /*
    라이브 퀴즈쇼 정보 조회 API
    */
    @Operation(summary = "라이브 퀴즈쇼 정보 조회", description = "라이브 퀴즈쇼 정보를 조회하는 메서드이다.")
    @GetMapping("/live/info")
    private ResponseEntity<QuizInfoResponse> getLiveQuizInfo() {

        return ResponseEntity.status(HttpStatus.OK).body(quizService.searchQuiz(LocalDateTime.now()));
    }

    /**
     * 그룹 퀴즈 정보 조회
     * @return 그룹 퀴즈 정보 리스트
     */
    @Operation(summary = "그룹 퀴즈 정보 조회", description = "그룹 퀴즈 정보 리스트를 조회하는 메서드이다.")
    @GetMapping("/group/info")
    public ResponseEntity<List<GroupQuizInfoResponse>> getGroupQuizInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(quizService.searchGroupQuiz(LocalDateTime.now()));
    }

    /*
    라이브 퀴즈쇼 개설 APi
     */
    @Operation(summary = "퀴즈 개설", description = "라이브 퀴즈, 그룹 퀴즈를 개설하는 메서드이다.")
    @PostMapping("/create")
    private ResponseEntity<QuizCreateResponse> createQuiz(@RequestBody QuizCreateRequest quizCreateRequest) throws JsonProcessingException {

        // service로 보내는 Dto로 변환
        QuizCreateRequestToServiceDto serviceDto = quizCreateRequest.toServiceDto();

        // service 호출
        QuizCreateResponse quizCreateResponse = quizService.createQuiz(serviceDto);

        return ResponseEntity.status(HttpStatus.OK).body(quizCreateResponse);
    }


    @Operation(summary = "카프카 테스트", description = "테스트를 위해 임시로 만든 메서드이다.")
    @GetMapping("/test")
    private String testKafka() throws JsonProcessingException {
        SubmitRedis submitRedis = SubmitRedis.builder()
                .quizId(1L)
                .memberId(1L)
                .problemOrder(1)
                .submitContent(LocalDateTime.now().toString())
                .duringTime(123L)
                .build();

        log.info("Producer 설정 시작~");
        Properties properties = new Properties();

        log.info("Condukter에 연결 가즈아");
        properties.setProperty("bootstrap.servers", "grown-goblin-6324-us1-kafka.upstash.io:9092");
        properties.setProperty("security.protocol", "SASL_SSL");
        properties.setProperty("sasl.mechanism", "SCRAM-SHA-256");
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required     username=\"Z3Jvd24tZ29ibGluLTYzMjQkhTAwQ8OQdqDW4QiJUV8c1IHcQ9JhLIb9B-mY1aE\" password=\"MDE3Yzc1OTUtYzVmMS00YmNhLTlmNDItYjdmODg5ZmRiZDE0\";");

        log.info("Producer 설정 가즈아");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        log.info("Producer 소환");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);


        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 30; i++) {
                ObjectMapper objectMapper = new ObjectMapper();
                String valueStr = objectMapper.writeValueAsString(submitRedis);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", valueStr);

                producer.send(producerRecord, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {

                        if (e == null) {
                            log.info("Received new metadata \n" +
                                    "Topic : " + metadata.topic() + "\n" +
                                    "Partition : " + metadata.partition() + "\n" +
                                    "Offset : " + metadata.offset() + "\n" +
                                    "Timestamp : " + metadata.timestamp());
                        } else {
                            log.error("에러다 에러");
                        }
                    }
                });
            }
        }

        producer.flush();

        producer.close();

        return "확인";
    }

}
