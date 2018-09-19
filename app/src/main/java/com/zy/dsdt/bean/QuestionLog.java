package com.zy.dsdt.bean;

public class QuestionLog {
	private String qid;// 日志id
	private String qno;// 用户编号
	private int qmodule;// 答题模块 1顺序 2章节 3随机 4模拟 5错题
	private String qtime;// 答题时间
	private String qscore;// 答题成绩

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getQno() {
		return qno;
	}

	public void setQno(String qno) {
		this.qno = qno;
	}

	public int getQmodule() {
		return qmodule;
	}

	public void setQmodule(int qmodule) {
		this.qmodule = qmodule;
	}

	public String getQtime() {
		return qtime;
	}

	public void setQtime(String qtime) {
		this.qtime = qtime;
	}

	public String getQscore() {
		return qscore;
	}

	public void setQscore(String qscore) {
		this.qscore = qscore;
	}

	@Override
	public String toString() {
		return "QuestionLog [qid=" + qid + ", qno=" + qno + ", qmodule="
				+ qmodule + ", qtime=" + qtime + ", qscore=" + qscore + "]";
	}
}
