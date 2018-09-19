package com.zy.dsdt.utils;

import com.zy.dsdt.bean.BlankQuestion;
import com.zy.dsdt.bean.BlankQuestionP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2016/5/17.
 */
public class BlankQuestionUtils {
    public static BlankQuestionP getBlankQuestionP(BlankQuestion bq) {
        BlankQuestionP bqp = new BlankQuestionP();

        bqp.setBno(bq.getBno());
        bqp.setBstem(bq.getBstem());
        bqp.setBsanalysis(bq.getBsanalysis());
        bqp.setBchapter(bq.getBchapter());

        String answers = "";

        for (String answer : bq.getAnswerList()) {
            answers = answers + answer + "$";
        }

        bqp.setBanswers(answers);
        return bqp;
    }

    public static BlankQuestion getBlankQuestion(BlankQuestionP bqp) {
        BlankQuestion bq = new BlankQuestion();

        bq.setBno(bqp.getBno());
        bq.setBstem(bqp.getBstem());
        bq.setBsanalysis(bqp.getBsanalysis());
        bq.setBchapter(bqp.getBchapter());

        String[] answers = bqp.getBanswers().split("\\$");

        for (int i = 0; i < answers.length; i++) {
            bq.addAnswer(answers[i]);
        }

        return bq;
    }

    public static List<BlankQuestion> getBlankQuestionList(List<BlankQuestionP> blankQuestionPs) {
        List<BlankQuestion> blankQuestions = new ArrayList<BlankQuestion>();

        for (BlankQuestionP blankQuestionP : blankQuestionPs) {
            blankQuestions.add(getBlankQuestion(blankQuestionP));
        }

        return blankQuestions;
    }
}
