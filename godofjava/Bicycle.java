public class Bicycle {
    // 변수(상태) 시작
    private String color;	// 색상
    private String grip;	// 손잡이모양
    private int speed;		// 속도
    private int distance;	// 거리
    private int weight;		// 무게
    // 변수(상태) 끝
	
    // 생성자 추가
    public Bicycle(){
    }
    
    // 메소드(행위) 시작
    public void rideOn(){
    	// 올라탐
    }
    
    public void rideOff(){
    	// 내림
    }
    
    public void speedUp(){
    	speed = speed+5;
    }
    
    public void speedDown(){
    	speed = speed-10;
    }
    
    public void changeGear(){
    	// 기어 바꿈
    }
    
    // 현재 속도 출력
    public int getCurrentSpeed(){
    	return speed;
    }
    // 메소드(행위) 끝
}