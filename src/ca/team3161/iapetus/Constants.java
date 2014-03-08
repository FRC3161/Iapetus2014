/* Copyright (c) 2014, FRC3161
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice, this
*   list of conditions and the following disclaimer in the documentation and/or
*   other materials provided with the distribution.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ca.team3161.iapetus;

public class Constants {

    public static class Gamepad {
        public static final int PORT = 1;
        public static final float DEADZONE = 0.125f;
    }
    
    public static class Joystick {
        public static final int PORT = 2;
        public static final float DEADZONE = 0.125f;
    }
    
    public static class Game {
        public static final int AUTONOMOUS_SECONDS = 10;
    }
    
    public static class Positions {
        /* Angles. 0 is straight down, 90 is straight forward, 180 straight up... */
        public static final int INTAKE = 63;
        public static final int LOWGOAL = 80;
        public static final int TRUSS = 160;
        public static final int SHOOTING = 145;
        public static final int START = 180;
    }
    
    public static class Shooter {
        public static final float WINCH_SPEED = 0.65f;
        public static final float ROLLER_SPEED = 1f;
    }
}
