## Camera Calibration

Use the following calibration pattern to calibrate your camera (calculate intrinsic camera parameters and distortion). On android smartphones you can use the App CameraCal.
Don't forget to choose the correct resolution (! you have to calibrate your camera for each resolution, if you want to use more than one).

![alt text](https://github.com/hpotechius/DCAITI-Project/blob/master/Application/Camera_Calibration/pattern_chessboard.png)

The following intrinsic camera parameters will be calculated:  
* f_x and f_y (camera focal length)
* c_x and c_y (optical centers in pixel coordinates)
* k_1, k_2 and k_3 (radial distortion)
* p_1 and p_2 (tangential distortion)