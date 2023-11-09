interface SessionMessage {
    quizId: number;
    type: string;
}

interface ChatMessage extends SessionMessage {
    memberId: number;
    message: string;
}

interface ProblemMessage extends SessionMessage {
    question: string;
    order: number;
    answerLength: number;
    selections: string[];
}

interface SubmitMessage extends SessionMessage {
    memberId: number;
    problemOrder: number;
    submitContent: string;
}

interface AnswerMessage extends SessionMessage {
    answer: string;
    correctNumber: number;
    totalNumber: number;
}

interface ResultMessage extends SessionMessage {
    gradingResultPresentResponseList: {
        memberId: number;
        nickName: string;
        correctProblemCount: number;
    }[];
}

export type {
    SessionMessage,
    ChatMessage,
    ProblemMessage,
    SubmitMessage,
    AnswerMessage,
    ResultMessage,
};
