import java.awt.AWTException;
import java.awt.Robot;


public class Test {
	public static void main(String[] args) throws AWTException {
		Robot robot = new Robot();
		for (int i = 1;i<1000;i+=10) {
			robot.mouseMove(i, i);
			robot.delay(100);
		}
	}
}
