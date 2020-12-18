package model;

import sdk.StdDraw;

public class Pipe
{
	private final double WIDTH = 0.25;//pipe width
	private final double GAP_HEIGHT = 0.5;//height
	private final double MAX_GAP_POS = 0.2;//smallest gap distance from top
	private final double MIN_GAP_POS = 0.2;//smallest gap distance from bottom
	private double vel = -0.02;//velocity of the pipe
	private boolean scorePossible = true;//check if user has scored form this pipe

	private double top, left;//position of top and left of the pipe

	public Pipe(double start)
	{
		top =
			Math.random() * (2 - MAX_GAP_POS - GAP_HEIGHT - MIN_GAP_POS) -
			(1 - MIN_GAP_POS - GAP_HEIGHT);//random height of gap between MAX_GAP_POS and MIN_GAP_POS
		left = start;
	}

	public void movePipe()
	{
		left += vel;

		if (left + WIDTH < -1)//off the screen
		{
			left = 2 - WIDTH;//put pipe back on the right
			top =
				Math.random() * (2 - MAX_GAP_POS - GAP_HEIGHT - MIN_GAP_POS) -
				(1 - MIN_GAP_POS - GAP_HEIGHT);//random height of gap between MAX_GAP_POS and MIN_GAP_POS

			scorePossible = true;//render score
		}
	}

	public void draw()//draw top and bottom
	{
		StdDraw.picture(left + WIDTH / 2.0, top + 1, "resources/images/pipetop.png", WIDTH, 2);
		StdDraw.picture(left + WIDTH / 2.0, top - GAP_HEIGHT - 1, "resources/images/pipebot.png", WIDTH, 2);
	}

	public double[] getGap()//top, bot, left, right
	{
		return new double[]{top, top - GAP_HEIGHT, left, left + WIDTH};
	}

	public boolean getPoint(double[] birdPos)//if not yet scored on pipe and bird halfway through pipe
	{
		if (birdPos[0] > left + WIDTH / 2.0 && scorePossible)
		{
			scorePossible = false;
			return true;
		}
		else
		{
			return false;
		}
	}

	public void stop()
	{
		vel = 0;
	}
}
