Assignment 2 for the COMP3421 Computer Graphics course

Extensions:

- Avatar: Creation of a Pacman. The animation is visible using the keyboards UP, DOWN, LEFT and RIGHT to move the avatar around the map.
          The code is in Game.java, mostly on the method drawAvatar. 

- Night mode: Creation of a torch which shines in the direction that the avatar is facing. The keyboard C turns on/off the night mode.
              The code is in Game.java, mostly on the methods drawTorch and setUpLightning.

- Sun: Creation of a sun that moves and changes its color according to the time of day. The keyboard Z turns on/off the sun movement/color change.
       When the sun moves, it uses different coordinates than specified on the file for better visualization.
       The code is in Game.java, mostly on the methods drawSun and updateSun.


Other controls:

-> SPACE key turns on/off the wireframe on the terrain
-> In third person mode, the arrow keys will onyl move the avatar. To
move the camera, hold down shift while pressing the arrow keys