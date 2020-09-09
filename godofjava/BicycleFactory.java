public class BicycleFactory {
	public static void main(String[] args) {
		System.out.println("BicycleFactory is Running!");
        
        Bicycle bicycle1 = new Bicycle();
        Bicycle bicycle2 = new Bicycle();
        Bicycle bicycle3 = new Bicycle();
        Bicycle bicycle4 = new Bicycle();
		
		bicycle1.speedUp();	// 페달을 밟는 메소드 호출
        bicycle1.speedUp();	// 페달을 밟는 메소드 호출
        System.out.println("bicycle1 current speed = " + bicycle1.getCurrentSpeed());
        bicycle2.speedUp(); // 페달을 밟는 메소드 호출
        System.out.println("bicycle2 current speed = " + bicycle2.getCurrentSpeed());
        bicycle3.speedUp();	// 페달을 밟는 메소드 호출
        System.out.println("bicycle3 current speed = " + bicycle3.getCurrentSpeed());
        bicycle4.speedUp(); // 페달을 밟는 메소드 호출
        bicycle4.speedUp(); // 페달을 밟는 메소드 호출
        System.out.println("bicycle4 current speed = " + bicycle4.getCurrentSpeed());
        
        bicycle1.speedDown();	// 브레이크를 잡는 메소드 호출
        System.out.println("bicycle1 current speed = " + bicycle1.getCurrentSpeed());
        bicycle4.speedDown();	// 브레이크를 잡는 메소드 호출
        System.out.println("bicycle4 current speed = " + bicycle4.getCurrentSpeed());
	}
}