package com.zy.dsdt.utils;

import android.content.Context;

import com.zy.dsdt.bean.BlankQuestion;
import com.zy.dsdt.bean.BlankQuestionP;
import com.zy.dsdt.bean.BlankQuestionPDao;
import com.zy.dsdt.bean.ChoiceQuestion;
import com.zy.dsdt.bean.ChoiceQuestionDao;
import com.zy.dsdt.bean.QuestionConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Chen on 2016/5/17.
 */
public class QuestionUtils {
    private int count;// 抽题总数
    private QuestionConfig qc;

    private ChoiceQuestionDao choiceQuestionDao;
    private BlankQuestionPDao blankQuestionPDao;

    public int count_choice;// 选择题数
    public int count_blank;// 填空题数
    public int choiceChapter[] = new int[9];
    public int blankChapter[] = new int[9];

    public QuestionUtils(DBManager manager, int count, QuestionConfig qc) {

        this.count = count;
        this.qc = qc;

        choiceQuestionDao = manager.getChoiceQuestionDao();
        blankQuestionPDao = manager.getBlankQuestionPDao();

        initChoice();
        initBlank();
    }

    private void initChoice() {
        int total_weight = 0;
        int choice_weight = qc.getChoiceQuestionWeight();
        int blank_weight = qc.getBlankQuestionWeight();
        List<Integer> weights = qc.getChchpaterweightList();

        count_choice = choice_weight * count / (choice_weight + blank_weight);

        for (Integer weight : weights) {
            total_weight = total_weight + weight;
        }

        int sum = 0;// 当前已分配题目数
        for (int i = 0; i < 9; i++) {
            choiceChapter[i] = weights.get(i) * count_choice / total_weight;
            sum = sum + choiceChapter[i];
        }

        choiceChapter[0] = choiceChapter[0] + count_choice - sum;
    }

    private void initBlank() {
        int total_weight = 0;
        List<Integer> weights = qc.getBlchapterweightList();

        count_blank = count - count_choice;

        for (Integer weight : weights) {
            total_weight = total_weight + weight;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            blankChapter[i] = weights.get(i) * count_blank / total_weight;
            sum = sum + blankChapter[i];
        }

        blankChapter[0] = blankChapter[0] + count_blank - sum;
    }

    public List<ChoiceQuestion> getChoiceQuestionList() {
        List<ChoiceQuestion> resultList = new ArrayList<ChoiceQuestion>();

        List<ChoiceQuestion> choiceQuestions = new ArrayList<ChoiceQuestion>();

        //组装所有抽取的题目
        for (int i = 1; i <= 9; i++) {
            if (choiceChapter[i - 1] != 0) {
                choiceQuestions.addAll(getChoiceQuestionOfChapter(i));
            }
        }

        List<Integer> integers = randomList(count_choice, count_choice);

        for (Integer i : integers) {
            resultList.add(choiceQuestions.get(i));
        }
        return resultList;
    }

    /**
     * 获取chapter章节的题目
     *
     * @param chapter
     * @param
     * @return
     */
    public List<ChoiceQuestion> getChoiceQuestionOfChapter(int chapter) {
        int index = chapter - 1;

        List<ChoiceQuestion> resultList = new ArrayList<ChoiceQuestion>();

        List<ChoiceQuestion> choiceQuestions = choiceQuestionDao.
                queryBuilder().
                where(ChoiceQuestionDao.Properties.Chchapter.eq(chapter)).
                list();

        List<Integer> integers = randomList(choiceChapter[index], choiceQuestions.size());

        for (Integer i : integers) {
            resultList.add(choiceQuestions.get(i));
        }
        return resultList;
    }

    public List<BlankQuestion> getBlankQuestionList() {
        List<BlankQuestion> resultList = new ArrayList<BlankQuestion>();

        List<BlankQuestion> blankQuestions = new ArrayList<BlankQuestion>();

        //组装所有抽取的题目
        for (int i = 1; i <= 9; i++) {
            if (blankChapter[i - 1] != 0) {
                blankQuestions.addAll(getBlankQuestionOfChapter(i));
            }
        }

        List<Integer> integers = randomList(count_blank, count_blank);

        for (Integer i : integers) {
            resultList.add(blankQuestions.get(i));
        }
        return resultList;
    }

    public List<BlankQuestion> getBlankQuestionOfChapter(int chapter) {
        int index = chapter - 1;

        List<BlankQuestionP> resultList = new ArrayList<BlankQuestionP>();

        List<BlankQuestionP> blankQuestionPs = blankQuestionPDao.
                queryBuilder().
                where(BlankQuestionPDao.Properties.Bchapter.eq(chapter)).
                list();

        List<Integer> integers = randomList(blankChapter[index], blankQuestionPs.size());

        for (Integer i : integers) {
            resultList.add(blankQuestionPs.get(i));
        }
        return BlankQuestionUtils.getBlankQuestionList(resultList);
    }

    /**
     * 产生n个f以内的随机数
     *
     * @param n
     * @param f
     * @return
     */
    public static List<Integer> randomList(int n, int f) {
        List<Integer> ints = new ArrayList<Integer>();

        if (n > f) {
            n = f;
        }

        int i = 0;
        while (i < n) {
            Random random = new Random();
            int k = random.nextInt(f);

            if (!ints.contains(k)) {
                ints.add(k);
                i++;
            }
        }

        return ints;
    }

    @Override
    public String toString() {
        return "QuestionUtils{" +
                "总题目数:" + count +
                ", 选择题目数:" + count_choice +
                ", 填空题目数:" + count_blank +
                ", 选择题各章题目数:" + Arrays.toString(choiceChapter) +
                ", 选择题各章题目数:" + Arrays.toString(blankChapter) +
                '}';
    }
}
