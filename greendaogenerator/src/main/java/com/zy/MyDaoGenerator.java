package com.zy;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    public static final String DAO_PATH = "./app/src/main/java-gen";
    public static final String PACKAGE_NAME = "com.greendao.bean";
    public static final int DATA_VERSION_CODE = 1;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DATA_VERSION_CODE, PACKAGE_NAME);

        addUser(schema);
        addUserClass(schema);
        addChoiceQuestion(schema);
        addBlankQuestionP(schema);
        addWrongQuestion(schema);

        //生成Dao文件路径
        new DaoGenerator().generateAll(schema, DAO_PATH);

    }

    private static void addWrongQuestion(Schema schema) {
        Entity wrongQuestion = schema.addEntity("WrongQuestion");

        wrongQuestion.addStringProperty("qno").primaryKey();
        wrongQuestion.addStringProperty("uno");
        wrongQuestion.addIntProperty("type");
    }

    private static void addChoiceQuestion(Schema schema) {
        Entity choiceQuestion = schema.addEntity("ChoiceQuestion");

        choiceQuestion.addStringProperty("chno").primaryKey();
        choiceQuestion.addStringProperty("chstem");
        choiceQuestion.addStringProperty("chanalysis");
        choiceQuestion.addIntProperty("chchapter");
        choiceQuestion.addIntProperty("chanswer");
        choiceQuestion.addStringProperty("chA");
        choiceQuestion.addStringProperty("chB");
        choiceQuestion.addStringProperty("chC");
        choiceQuestion.addStringProperty("chD");

    }

    private static void addBlankQuestionP(Schema schema) {
        Entity blankQuestion = schema.addEntity("BlankQuestionP");

        blankQuestion.addStringProperty("bno").primaryKey();
        blankQuestion.addStringProperty("bstem");
        blankQuestion.addStringProperty("bsanalysis");
        blankQuestion.addIntProperty("bchapter");
        blankQuestion.addStringProperty("banswers");

    }

    private static void addUserClass(Schema schema) {
        Entity userClass = schema.addEntity("UserClass");
        userClass.addStringProperty("cno").primaryKey();
        userClass.addStringProperty("cname");
        userClass.addStringProperty("cschool");
        userClass.addStringProperty("cteacher");
    }

    private static void addUser(Schema schema) {

        Entity user = schema.addEntity("User");
        user.addStringProperty("uno").primaryKey();
        user.addStringProperty("uname");
        user.addStringProperty("upassword");
        user.addStringProperty("uclass");
        user.addStringProperty("uclassname");
        user.addStringProperty("uschool");
        user.addStringProperty("uteacher");
        user.addIntProperty("upermission");
        user.addIntProperty("ucredit");

    }

}
