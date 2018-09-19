package com.zy.dsdt.bean;

import java.util.ArrayList;
import java.util.List;

public class QuestionConfig {
	private int choiceQuestionWeight;
	private int blankQuestionWeight;
	private List<Integer> chchpaterweightList;
	private List<Integer> blchapterweightList;

	public QuestionConfig() {
		chchpaterweightList = new ArrayList<Integer>();
		blchapterweightList = new ArrayList<Integer>();
	}

	public int getQuestionWeight(int qtype) {
		if (qtype == 1) {
			return choiceQuestionWeight;
		} else {
			return blankQuestionWeight;
		}
	}

	public List<Integer> getQuestionChapterWeightList(int qtype) {
		if (qtype == 1) {
			return chchpaterweightList;
		} else {
			return blchapterweightList;
		}
	}

	public int getChoiceQuestionWeight() {
		return choiceQuestionWeight;
	}

	public void setChoiceQuestionWeight(int choiceQuestionWeight) {
		this.choiceQuestionWeight = choiceQuestionWeight;
	}

	public int getBlankQuestionWeight() {
		return blankQuestionWeight;
	}

	public void setBlankQuestionWeight(int blankQuestionWeight) {
		this.blankQuestionWeight = blankQuestionWeight;
	}

	public List<Integer> getChchpaterweightList() {
		return chchpaterweightList;
	}

	public void setChchpaterweightList(List<Integer> chchpaterweightList) {
		this.chchpaterweightList = chchpaterweightList;
	}

	public List<Integer> getBlchapterweightList() {
		return blchapterweightList;
	}

	public void setBlchapterweightList(List<Integer> blchapterweightList) {
		this.blchapterweightList = blchapterweightList;
	}

	@Override
	public String toString() {
		return "QuestionConfig [choiceQuestionWeight=" + choiceQuestionWeight
				+ ", blankQuestionWeight=" + blankQuestionWeight
				+ ", chchpaterweightList=" + chchpaterweightList
				+ ", blchapterweightList=" + blchapterweightList + "]";
	}
}
