# Retro Breaker - a brick breaker game with a retro style for Android

## Introduction

Retro Breaker is a simple brick breaker game with an old, retro appearance. This game was created for the Mobile Computing course (MAC5743) on IME-USP, 2014 class. It has the following game mechanics:

- **Retro graphics**: no textures, just plain colors and simple animation;
- **Sound effects**: different sound effects for each possible game event (e.g. hitting a brick, hitting a wall, hitting the paddle...);
- **Score multiplier**: each time the ball breaks a brick, it increases the score multiplier for the next hit. If the ball hits the paddle, this score multiplier is reduced. If the player loses a life, this score multiplier is reset. This is to incentive the player to hit consecutive bricks;
- **Multiple brick types**: there is four types of brick:
	+ *White bricks*: this is the basic brick, it just waits to be broken;
	+ *Red bricks*: this bricks explodes, breaking all bricks around it and leaving a nice explosion effect;
	+ *Gray bricks*: this is almost the same as the White brick, but needs two hits instead of one to break;
	+ *Green bricks*: this bricks moves horizontally if there isn't any obstacle. Try to break this brick before breaking his neighborhood, this will make things easier.
- **Random generated levels with different difficult settings**: there are three different difficult settings (*Easy*, *Normal* and *Hard*; except for *Can't die* that exist mostly for debugging purposes, since you can't score); each difficult has a different number of stock lives, hit score, max score multiplier, ball speed and generates a random level with the number of special bricks according to a probability:
	+ *Easy*: the player starts with 3 stock lives, when you finish the game each extra live gives you 2500 bonus points, each hit has a base score of 50, the maximum score multiplier is 4x, the ball speed is slow, there is 15% of probability to appears both grey and red bricks and no probability to appear green bricks;
	+ *Normal*: the player starts with 2 stock lives, when you finish the game each extra live gives you 5000 bonus points, each hit has a base score of 100, the maximum score multiplier is 8x, the ball speed is normal (or 1.5x faster than on *Easy* difficult), there is 25% of probability to appear grey bricks, 10% to red bricks and 5% probability to appear green bricks;
	+ *Hard*: the player starts with 1 stock lives, when you finish the game each extra live gives you 15000 bonus points, each hit has a base score of 150, the maximum score multiplier is 16x, the ball speed is fast (or 2x faster than on *Easy* difficult), there is 35% of probability to appear grey bricks, 5% to red bricks and 10% probability to appear green bricks, while the green bricks itself moves faster.

This game has pretty simple mechanics, like the original game. If you feel even a little bit of nostalgia, our objective was completed.

## Instructions

Select the desired difficult on main activity (this selected difficult is stored so you don't need to change again on the next time you play) and press "New Game" button to start a new game. Pressing any part of the screen start moving the ball. The paddle is controlled by moving your finger on screen (on any place, you don't need to actually press the paddle).

When the game is over you have the option to return to the previous screen (in this case, the main activity) or to start a new game.

If you exit the application the game is paused automatically. You don't need to press anything.

## Use the source, Luke!

This game was made on [Android Developer Tools](https://developer.android.com/tools/index.html), so it's easier to simple import this code on your Eclipse workspace. Just go to ```File->Import->Android->Existing Android Code Into Workspace``` or, if the EGit plug-in installed, ```File->Import->Git->Projects from Git``` and point the URI from this Git repository. You need Android SDK 4.4.2, since the application does use newer funcionality when it makes sense (like [Holo theme](https://developer.android.com/design/style/themes.html) and [Immersive mode](https://developer.android.com/training/system-ui/immersive.html)).

The code is organized in the following way:

- ```br.usp.ime.retrobreaker```: activity related code:
	+ *MainActivity*: the first activity that appears when you open the app; not related with the game *per se*, but include options that change the game in some way;
	+ *GameActivity*: renders the OpenGL view and the user interface on screen (including actual/high score, score multiplier, number of lives and "Ready?" text).
- ```br.usp.ime.retrobreaker.game```: the main game code:
	+ *Constants*: all game constants in one place so we can easily change them;
	+ *Game*: the main game logic, including drawing methods, game physics, game state methods, etc.;
	+ *TouchSurfaceView*: include OpenGL logic and methods to get the user finger position on screen;
- ```br.usp.ime.retrobreaker.forms```: the various objects rendered on screen:
	+ *Quad*: the base class of all forms. This is a abstract class, so you need to extend it first before using it;
	+ *Ball/Brick/MobileBrick/Paddle/Particle*: extend classes of Quad that represents the diverse objects of the screen.
- ```br.usp.ime.retrobreaker.effects```: create special effects on screen:
	+ *Explosion*: generates a explosion effect on ball hit.

## Compatibility

This application should work on any Android device from version 2.2 up to the most recent version of Android (to this date, 4.4.2 KitKat). This application was tested on the following devices:

- Moto X (running Android 4.4.2);
- LG G Pad 8.3'' (running CyanogenMod 11M6, Android 4.4.2);
- [Genymotion](http://www.genymotion.com/) Custom Phone 7 (running Android 4.4.2);
- AOC Breeze 7'' 7Y2241 (running Android 4.1.1);
- Android emulator (tested on 2.2, 2.3.3 and 4.4.2 versions).

The game should work better if your device has a screen ratio of 16:9 (or 9:16 if you're couting portrait mode), since this is the internal resolution assumed by the code. Any other screen ratio will add borders to the game.

## Known bugs

Sometimes, the ball will reflect infinitely on paddle or on the wall. In the paddle case just keep moving the paddle until the ball starts moving again. In the wall case the user will probably lose a life, or the ball will keep reflecting infinitely on limbo. Both bugs should be fixed on the most commons cases but this code still need more test.

## License

This code is licensed on MIT license (see ```LICENSE``` file for details) except if the file itself says otherwise. The exception is the audio files, that we are using either on a [fair use](https://en.wikipedia.org/wiki/Fair_use) basis or we are using royalty free music. But do understand that even when the music files are royalty free, you can't resell them (there was a clause on any site that I downloaded the music files saying that I could not sell them).

## Credits

The game was created by [Hugo Vinicius Vaz Braga](http://www.ime.usp.br/~hbraga/) and [Thiago Kenji Okada](http://www.ime.usp.br/~thiagoko/) for MAC5743 course on IME-USP, 2014 class. Other people/companies that helped with the creation of this game includes:

- [Alfredo Goldman vel Lejbman](http://www.ime.usp.br/~gold/): the teaching professor of this course;
- [Top Free Games](http://www.topfreegames.com/): for the original code (TechTalkDemo) and the talk that they give showing how the actual mobile game market is;
- [Antonio Deusany de Carvalho Junior](http://www.ime.usp.br/~dj/): one of the monitors of this class;
- [Gilmar Rocha de Oliveira Dias](http://www.ime.usp.br/~grodias/): the other monitor;
- Wilson Kazuo Mizutani: one of the students in the class that already knew how to make games; helped a lot with our OpenGL problems.

And of course, all the people that was part of the MAC0643/5743 class.
