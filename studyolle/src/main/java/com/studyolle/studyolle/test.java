package com.studyolle.studyolle;

public class test {


    // private final String name;
    // 이건 왜 에러가 날까요?
    // final 키워드는 클래스에 붙일경우 상속을 못 받게 만들고, 메소드에 붙일경우 자식 클래스에서 재정의하지 못하게 만듬 변수에 붙일 경우 변수의 값을 변경할 수 없음. 다시 말해서 한번만 할당 가능한 읽기 전용 변수임
    // 그런데 위에서 name 변수는 초기화가 안된 상태로 변수 선언이 끝났기 떄문에 이 변수는 아무 값도 없고, 넣을수도 없는 상태의 변수가 되기 때문에 빌드에러를 보여주는 듯.

    // 아래 헬로우와 하이는 언제찍힐까? , 메인에서 인스턴스로 생성해서 쓸때 뭐가 먼저 나올까?
    static {
        System.out.println("hello");
    }

    private void printHi() {
        System.out.println("hi");
    }

    // 아래와 같이 변수와 게터, toString 함수가 있고, 메인에서 t.getNumber().toString(); 호출하면 이 클래스에 toString메소드가 있기 때문에 문자열이 나오는건가요?
    private Integer number;
    private String name;

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "test{" +
                "number=" + number +
                ", name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) {
        test t = new test();
        t.printHi();

        t.getNumber().toString();
    }



}
