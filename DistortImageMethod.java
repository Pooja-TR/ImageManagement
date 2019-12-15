package magick;

public interface DistortImageMethod {
	public final static int UndefinedDistortion = 0;
	public final static int AffineDistortion = 1;
	public final static int AffineProjectionDistortion = 2;
	public final static int ScaleRotateTranslateDistortion =  3;
	public final static int PerspectiveDistortion =  4;
	public final static int PerspectiveProjectionDistortion =  5;
	public final static int BilinearForwardDistortion = 6;
	public final static int BilinearDistortion = 7;
	public final static int BilinearReverseDistortion = 8;
	public final static int PolynomialDistortion = 9;
	public final static int ArcDistortion = 10;
	public final static int PolarDistortion = 11;
	public final static int DePolarDistortion = 12;
	public final static int BarrelDistortion = 13;
	public final static int BarrelInverseDistortion = 14;
	public final static int ShepardsDistortion = 15;
	public final static int SentinelDistortion = 16;
}