package com.aariyan.imo_admin.Model;

public class PointModel {
    private String questionPoint,wrongAnswerPoint,rightAnswerPoint;

    public PointModel(){}

    public PointModel(String questionPoint, String wrongAnswerPoint, String rightAnswerPoint) {
        this.questionPoint = questionPoint;
        this.wrongAnswerPoint = wrongAnswerPoint;
        this.rightAnswerPoint = rightAnswerPoint;
    }

    public String getQuestionPoint() {
        return questionPoint;
    }

    public void setQuestionPoint(String questionPoint) {
        this.questionPoint = questionPoint;
    }

    public String getWrongAnswerPoint() {
        return wrongAnswerPoint;
    }

    public void setWrongAnswerPoint(String wrongAnswerPoint) {
        this.wrongAnswerPoint = wrongAnswerPoint;
    }

    public String getRightAnswerPoint() {
        return rightAnswerPoint;
    }

    public void setRightAnswerPoint(String rightAnswerPoint) {
        this.rightAnswerPoint = rightAnswerPoint;
    }
}
