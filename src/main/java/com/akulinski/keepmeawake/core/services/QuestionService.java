package com.akulinski.keepmeawake.core.services;

import com.akulinski.keepmeawake.core.domain.Question;
import com.akulinski.keepmeawake.core.domain.dto.QuestionDTO;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    public Question mapDTO(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setCategory(questionDTO.getCategory());
        question.setDescription(questionDTO.getDescription());
        question.setValue(question.getValue());

        return question;
    }
}
