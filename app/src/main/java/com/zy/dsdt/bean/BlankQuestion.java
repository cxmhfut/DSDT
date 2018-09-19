package com.zy.dsdt.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * BlankQuestion 填空题类
 * 
 * @author Chen
 *
 */
public class BlankQuestion {
	private String bno;// 题目编号
	private String bstem;// 题目题干
	private String bsanalysis;// 题目解析
	private int bchapter;// 题目章节
	private List<String> banswers;// 题目答案集合

	public BlankQuestion() {
		banswers = new ArrayList<String>();
	}

	public String getBno() {
		return bno;
	}

	public void setBno(String bno) {
		this.bno = bno;
	}

	public int getBchapter() {
		return bchapter;
	}

	public void setBchapter(int bchapter) {
		this.bchapter = bchapter;
	}

	public String getBstem() {
		return bstem;
	}

	public void setBstem(String bstem) {
		this.bstem = bstem;
	}

	public String getBsanalysis() {
		return bsanalysis;
	}

	public void setBsanalysis(String bsanalysis) {
		this.bsanalysis = bsanalysis;
	}

	public List<String> getBanswers() {
		return banswers;
	}

	public void setBanswers(List<String> banswers) {
		this.banswers = banswers;
	}

	/**
	 * 添加答案
	 * 
	 * @param answer
	 */
	public void addAnswer(String answer) {
		banswers.add(answer);
	}

	/**
	 * 得到position位置的答案
	 * 
	 * @param position
	 * @return
	 */
	public String getAnswer(int position) {
		return banswers.get(position - 1);
	}

	public List<String> getAnswerList() {
		return banswers;
	}

	public void setAnswerList(List<String> answers) {
		banswers = answers;
	}

	@Override
	public String toString() {
		return "BlankQuestion [bno=" + bno + ", bstem=" + bstem
				+ ", bsanalysis=" + bsanalysis + ", bchapter=" + bchapter
				+ ", banswers=" + banswers + "]";
	}

}
