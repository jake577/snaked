package com.jesttek.snakeWar;

public class Coordinate {
	public float X;
	public float Y;
	public Coordinate(float x, float y)
	{
		X = x;
		Y = y;
	}
	public Coordinate(Coordinate c)
	{
		X = c.X;
		Y = c.Y;
	}
	public Coordinate()
	{
		X = 0;
		Y = 0;
	}
}
