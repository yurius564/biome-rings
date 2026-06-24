package yurius.ringbiomes;

import java.util.Random;

public class NoiseCalculation {
	private final double centerX;
		private final double centerY;
		private final double baseRadius;
		
		private final double[] aCoefficients;
		private final double[] bCoefficients;
		private final int harmonics;

		public NoiseCalculation(double centerX, double centerY, double baseRadius, int harmonics, double maxAmplitude, long seed) {
			this.centerX = centerX;
			this.centerY = centerY;
			this.baseRadius = baseRadius;
			this.harmonics = harmonics;
			
			this.aCoefficients = new double[harmonics];
			this.bCoefficients = new double[harmonics];
			
			Random random = new Random(seed);
			for (int i = 0; i < harmonics; i++) {
				int k = i + 1;
				this.aCoefficients[i] = (random.nextDouble() * 2 - 1) * (maxAmplitude / k);
				this.bCoefficients[i] = (random.nextDouble() * 2 - 1) * (maxAmplitude / k);
			}
		}

		public boolean isPointOnDeformedEdge(double x, double y, double epsilon) {
			double dx = x - centerX;
			double dy = y - centerY;
			double realRadius = Math.sqrt(dx * dx + dy * dy);
			double theta = Math.atan2(dy, dx);
			double deformedRadius = baseRadius;
			for (int i = 0; i < harmonics; i++) {
				int k = i + 1;
				deformedRadius += aCoefficients[i] * Math.cos(k * theta) 
												+ bCoefficients[i] * Math.sin(k * theta);
			}

			return Math.abs(realRadius - deformedRadius) <= epsilon;
		}

		public static void main(String[] args) {
			NoiseCalculation circle = new NoiseCalculation(0, 0, 100, 5, 15, 424242L);
			double testX = 95.4;
			double testY = 22.1;
			double tolerance = 1.0;

			boolean onEdge = circle.isPointOnDeformedEdge(testX, testY, tolerance);
			System.out.println("O ponto está na borda deformada? " + onEdge);
		}
}
