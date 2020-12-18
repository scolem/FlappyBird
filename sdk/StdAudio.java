package sdk;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.DataLine.Info;

public final class StdAudio {
	public static final int SAMPLE_RATE = 44100;
	private static final int BYTES_PER_SAMPLE = 2;
	private static final int BITS_PER_SAMPLE = 16;
	private static final double MAX_16_BIT = 32767.0D;
	private static final int SAMPLE_BUFFER_SIZE = 4096;
	private static SourceDataLine line;
	private static byte[] buffer;
	private static int bufferSize = 0;

	private StdAudio() {
	}

	private static void init() {
		try {
			AudioFormat var0 = new AudioFormat(44100.0F, 16, 1, true, false);
			Info var1 = new Info(SourceDataLine.class, var0);
			line = (SourceDataLine)AudioSystem.getLine(var1);
			line.open(var0, 8192);
			buffer = new byte[2730];
		} catch (Exception var2) {
			System.out.println(var2.getMessage());
			System.exit(1);
		}

		line.start();
	}

	public static void close() {
		line.drain();
		line.stop();
	}

	public static void play(double var0) {
		if (var0 < -1.0D) {
			var0 = -1.0D;
		}

		if (var0 > 1.0D) {
			var0 = 1.0D;
		}

		short var2 = (short)((int)(32767.0D * var0));
		buffer[bufferSize++] = (byte)var2;
		buffer[bufferSize++] = (byte)(var2 >> 8);
		if (bufferSize >= buffer.length) {
			line.write(buffer, 0, buffer.length);
			bufferSize = 0;
		}

	}

	public static void play(double[] var0) {
		for(int var1 = 0; var1 < var0.length; ++var1) {
			play(var0[var1]);
		}

	}

	public static double[] read(String var0) {
		byte[] var1 = readByte(var0);
		int var2 = var1.length;
		double[] var3 = new double[var2 / 2];

		for(int var4 = 0; var4 < var2 / 2; ++var4) {
			var3[var4] = (double)((short)(((var1[2 * var4 + 1] & 255) << 8) + (var1[2 * var4] & 255))) / 32767.0D;
		}

		return var3;
	}

	public static void play(String var0) {
		URL var1 = null;

		try {
			File var2 = new File(var0);
			if (var2.canRead()) {
				var1 = var2.toURI().toURL();
			}
		} catch (MalformedURLException var3) {
			var3.printStackTrace();
		}

		if (var1 == null) {
			throw new RuntimeException("audio " + var0 + " not found");
		} else {
			AudioClip var4 = Applet.newAudioClip(var1);
			var4.play();
		}
	}

	public static void loop(String var0) {
		URL var1 = null;

		try {
			File var2 = new File(var0);
			if (var2.canRead()) {
				var1 = var2.toURI().toURL();
			}
		} catch (MalformedURLException var3) {
			var3.printStackTrace();
		}

		if (var1 == null) {
			throw new RuntimeException("audio " + var0 + " not found");
		} else {
			AudioClip var4 = Applet.newAudioClip(var1);
			var4.loop();
		}
	}

	private static byte[] readByte(String var0) {
		Object var1 = null;
		AudioInputStream var2 = null;

		try {
			File var3 = new File(var0);
			byte[] var6;
			if (var3.exists()) {
				var2 = AudioSystem.getAudioInputStream(var3);
				var6 = new byte[var2.available()];
				var2.read(var6);
			} else {
				URL var4 = StdAudio.class.getResource(var0);
				var2 = AudioSystem.getAudioInputStream(var4);
				var6 = new byte[var2.available()];
				var2.read(var6);
			}

			return var6;
		} catch (Exception var5) {
			System.out.println(var5.getMessage());
			throw new RuntimeException("Could not read " + var0);
		}
	}

	public static void save(String var0, double[] var1) {
		AudioFormat var2 = new AudioFormat(44100.0F, 16, 1, true, false);
		byte[] var3 = new byte[2 * var1.length];

		for(int var4 = 0; var4 < var1.length; ++var4) {
			short var5 = (short)((int)(var1[var4] * 32767.0D));
			var3[2 * var4 + 0] = (byte)var5;
			var3[2 * var4 + 1] = (byte)(var5 >> 8);
		}

		try {
			ByteArrayInputStream var7 = new ByteArrayInputStream(var3);
			AudioInputStream var8 = new AudioInputStream(var7, var2, (long)var1.length);
			if (!var0.endsWith(".wav") && !var0.endsWith(".WAV")) {
				if (!var0.endsWith(".au") && !var0.endsWith(".AU")) {
					throw new RuntimeException("File format not supported: " + var0);
				}

				AudioSystem.write(var8, Type.AU, new File(var0));
			} else {
				AudioSystem.write(var8, Type.WAVE, new File(var0));
			}
		} catch (Exception var6) {
			System.out.println(var6);
			System.exit(1);
		}

	}

	private static double[] note(double var0, double var2, double var4) {
		int var6 = (int)(44100.0D * var2);
		double[] var7 = new double[var6 + 1];

		for(int var8 = 0; var8 <= var6; ++var8) {
			var7[var8] = var4 * Math.sin(6.283185307179586D * (double)var8 * var0 / 44100.0D);
		}

		return var7;
	}

	public static void main(String[] var0) {
		double var1 = 440.0D;

		for(int var3 = 0; var3 <= 44100; ++var3) {
			play(0.5D * Math.sin(6.283185307179586D * var1 * (double)var3 / 44100.0D));
		}

		int[] var7 = new int[]{0, 2, 4, 5, 7, 9, 11, 12};

		for(int var4 = 0; var4 < var7.length; ++var4) {
			double var5 = 440.0D * Math.pow(2.0D, (double)var7[var4] / 12.0D);
			play(note(var5, 1.0D, 0.5D));
		}

		close();
		System.exit(0);
	}

	static {
		init();
	}
}
