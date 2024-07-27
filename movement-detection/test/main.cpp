#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>

using namespace std;
using namespace cv;

Rect getBestContour(vector<vector<Point>>& contours, int treshold = 5000) {
	int bestArea = treshold;
	Rect bestContour;

	for (vector<Point> c : contours) {
		int area = contourArea(c);

		if (area > bestArea) {
			bestArea = area;
			bestContour = boundingRect(c);
		}
	}

	return bestContour;
}

int main() {
	VideoCapture cap(0); // 640 x 480
	Mat prevFrame, currFrame;

	Mat dilateKernel = getStructuringElement(MORPH_RECT, Size(2, 3));
	Mat diff, gray, blur, tresh, dilated;
	vector<vector<Point>> contours;

	while (true) {
		// Get frames
		cap.read(prevFrame);
		cap.read(currFrame);

		// Preprocessing
		absdiff(currFrame, prevFrame, diff);
		cvtColor(diff, gray, COLOR_BGR2GRAY);
		GaussianBlur(gray, blur, Size(21,21), 0);
		threshold(blur, tresh, 20, 255, THRESH_BINARY);
		dilate(tresh, dilated, dilateKernel);
		
		// Analyze contours
		findContours(dilated, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
		Rect bestR = getBestContour(contours);
		rectangle(currFrame, bestR, Scalar(0, 255, 0), 2);
		
		// Show current frame
		imshow("Motion Detection", currFrame);
		if (waitKey(30) == 'q') break;
	}

	destroyAllWindows();
	cap.release();

	return 0;
}