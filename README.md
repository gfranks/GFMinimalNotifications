GFMinimalNotification
===========

GFMinimalNotifications was inspired by JFMinimalNotifications created by Jeremy Fox (https://github.com/atljeremy/JFMinimalNotifications)

This is an Android controller for presenting a beautiful notification that is highly configurable and works for both phone and tablet. GFMinimalNotification is only available for API levels 15+.

What It Looks Like:
------------------

![Example](/resources/sample_video_gif_top.gif) ![Example](/resources/sample_video_gif_bottom.gif)

See a short video of this control here:

[![Sample Video](http://img.youtube.com/vi/EOMgqaOqIoQ/0.jpg)](https://www.youtube.com/watch?v=EOMgqaOqIoQ)

### Screen Shots

![Examples](/resources/screenshot_error.png?raw=true) ![Examples](/resources/screenshot_default.png?raw=true) ![Examples](/resources/screenshot_info.png?raw=true) ![Examples](/resources/screenshot_warning.png?raw=true) ![Examples](/resources/screenshot_success.png?raw=true)

How To Use It:
-------------

### Basic Example

```java
/**
 * To use the GFMinimalNotification, you must either, extend one of the provided activity or fragment classes,
 * pass the container view you wish to display the notification in, or you may include the provided GFMinimalNotificationLayout in your xml file.
 * You may display this in the activity's window decor view as well.
 */
public class MainActivity extends BaseNotificationActionBarActivity {

    private GFMinimalNotification minimalNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate with Constructor method
        minimalNotification = new GFMinimalNotification(this, GFMinimalNotificationStyle.DEFAULT,
                "This is my awesome title", "This is my awesome sub-title");

        // instantiate with Builder
        minimalNotification = new GFMinimalNotification.Builder(this)
                                              .title("This is my awesome title")
                                              .subtitle("This is my awesome sub-title")
                                              .style(GFMinimalNotificationStyle.DEFAULT)
                                              .build();
    }
    
    /**
     * Showing the notification from a method
     */
    public void show() {
        minimalNotification.show(this);
    }

    /**
     * Showing the notification inside a container from a method
     */
    public void showInView() {
        minimalNotification.show(findViewById(<id_of_your_container_view>));
    }

    /**
     * Showing the notification in the decor view from a method. If dispalyed from the top, this will overlay the actionbar/toolbar
     */
    public void showFromDecor() {
        minimalNotification.show(this);
    }

    /**
     * Hiding the notification from a method
     */
    public void dismiss() {
        minimalNotification.dismiss();
    }

    /**
     * Set the notification to display from the top of the screen
     */
    public void setDisplayFromTop() {
        minimalNotification.setSlideDirection(GFMinimalNotification.SLIDE_TOP);
    }

    /**
     * Set the notification to display from the bottom of the screen
     */
    public void setDisplayFromBottom() {
        minimalNotification.setSlideDirection(GFMinimalNotification.SLIDE_BOTTOM);
    }
}
```

Want to use the new ```Toolbar```? You can! Just extend ```BaseNotificationToolbarActivity``` to enable toolbar use. Or if you want to use your own implementation, copy this library into your project or use the ```show(...)``` (passing the activity or fragment instance) or ```show(findViewById(<id_of_your_container_view>)``` option when displaying your notification.

### Constructors / Options

```java
/**
 * Note: passing a duration of 0 means the notification will NOT be automatically dismissed, you will need to 
 * dismiss the notification yourself by calling dismiss() on the notification object. If you pass a duration 
 * value greater than 0, this will be the length of time the notification will remain visisble before being 
 * automatically dismissed. Examples follow. Please consult the GFMinimalNotification or GFMinimalNotificationLayout class to see all available
 * constructors/options.
 */
 
// With duration
minimalNotification = new GFMinimalNotification(this, GFMinimalNotificationStyle.DEFAULT,
                "This is my awesome title", "This is my awesome sub-title", GFMinimalNotification.LENGTH_LONG);
 
// Without duration and with onClick listener
minimalNotification = new GFMinimalNotification(this, GFMinimalNotificationStyle.DEFAULT,
                "This is my awesome title", "This is my awesome sub-title", 0)
                .setOnGFMinimalNotificationClickListener(new OnGFMinimalNotificationClickListener() {
                    @Override
                    public void onClick(GFMinimalNotification notification) {
                        notification.dismiss();
                    }
                });

// with Builder
minimalNotification = new GFMinimalNotification.Builder(this)
                                              .title("This is my awesome title")
                                              .subtitle("This is my awesome sub-title")
                                              .style(GFMinimalNotificationStyle.DEFAULT)
                                              .clickListener(new OnGFMinimalNotificationClickListener() {
                                                                  @Override
                                                                  public void onClick(GFMinimalNotification notification) {
                                                                      // dismiss if no duration
                                                                  }
                                                              })
                                              .build();
```

```java
// Available Styles
public enum GFMinimalNotificationStyle {
    DEFAULT,
    ERROR,
    SUCCESS,
    INFO,
    WARNING
}
```

Please see the sample project included in this repo for an example of how to use this notification.

### Undo Notifications

Want to use the ```GFMinimalNotification``` or ```GFMinimalNotificationLayout``` to display an undo option? Why not just use the ```GFUndoNotification``` or ```GFUndoNotificationLayout``` classes! This
library also includes this and can be used in conjunction with actions that may need an undo option. This notification will
display at the bottom by default but has the same presentation options as the ```GFMinimalNotification``` and ```GFMinimalNotificationLayout``` as it extends
those classes. However, this notification does not use styles. But you may use any of the accessor methods to modify any text or
background option on the undo notification.

```java
/**
 * Please consult the GFUndoNotification or GFUndoNotificationLayout class to see all available constructors/options.
 */
// With duration and title only
minimalNotification = new GFUndoNotification(this, "This is my awesome title", GFMinimalNotification.LENGTH_LONG);

// With duration and title/sub-title
minimalNotification = new GFUndoNotification(this, "This is my awesome title", "This is my awesome sub-title", GFMinimalNotification.LENGTH_LONG);

// Without duration and with onClick listener
minimalNotification = new GFUndoNotification(this, "This is my awesome title", 0)
                .setOnGFMinimalNotificationClickListener(new OnGFMinimalNotificationClickListener() {
                                    @Override
                                    public void onClick(GFMinimalNotification notification) {
                                        notification.dismiss();
                                    }
                                });

// with Builder
minimalNotification = new GFUndoNotification.Builder(this)
                                           .title("This is my awesome title")
                                           .subtitle("This is my awesome sub-title")
                                           .style(GFMinimalNotificationStyle.DEFAULT)
                                           .clickListener(new OnGFMinimalNotificationClickListener() {
                                                               @Override
                                                               public void onClick(GFMinimalNotification notification) {
                                                                   // dismiss if no duration
                                                               }
                                                           })
                                           .build();
```

#### Basic Undo Example

```java
/**
 * To use the GFUndoNotification, you must follow the same principles as expressed in the above example.
 */
public class MainActivity extends BaseNotificationActionBarActivity {

    private GFUndoNotification undoNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate with Constructor method
        undoNotification = new GFUndoNotification(this, "You did something")
                .setGFUndoNotificationCallback(new GFUndoNotificationCallback() {
                            @Override
                            public void onUndoAction(GFUndoNotification notification) {
                                // undo user performed action
                            }
                        });

        // instantiate with Builder
        undoNotification = new GFUndoNotification.Builder(this)
                                                .title("You did something")
                                                .undoCallback(new GFUndoNotificationCallback() {
                                                                    @Override
                                                                    public void onUndoAction(GFUndoNotification notification) {
                                                                        // undo user performed action
                                                                    }
                                                                })
                                                .build();
    }

    /**
     * Showing the undo notification from a method
     */
    public void show() {
        undoNotification.show(this);
    }

    /**
     * Showing the undo notification inside a container from a method
     */
    public void showInView() {
        undoNotification.show(findViewById(<id_of_your_container_view>));
    }

    /**
     * Showing the undo notification in the decor view from a method. If displayed from the top, this will overlay the actionbar/toolbar
     */
    public void showFromDecor() {
        undoNotification.show(this);
    }

    /**
     * Hiding the undo notification from a method
     */
    public void dismiss() {
        undoNotification.dismiss();
    }

    /**
     * Set the notification to display from the top of the screen
     */
    public void setDisplayFromTop() {
        undoNotification.setSlideDirection(GFMinimalNotification.SLIDE_TOP);
    }

    /**
     * Set the notification to display from the bottom of the screen
     */
    public void setDisplayFromBottom() {
        undoNotification.setSlideDirection(GFMinimalNotification.SLIDE_BOTTOM);
    }
}
```

Callback Methods:
----------------

    /**
     * GFMinimalNotificationCallback
     */
    public void didShowNotification(GFMinimalNotification notification);
    public void didDismissNotification(GFMinimalNotification notification);

    /**
     * GFUndoNotificationCallback
     */
    public void onUndoAction(GFUndoNotification notification);

Installation:
------------

### Directly include source into your projects

- Simply copy the source/resource files from the library folder into your project.

### Use binary approach

- Follow these steps to include aar binary in your project:

    1: Copy com.github.gfranks.minimal.notification-1.0.aar into your projects libs/ directory.

    2: Include the following either in your top level build.gradle file or your module specific one:
    ```
      repositories {
         flatDir {
             dirs 'libs'
         }
     }
    ```
    3: Under your dependencies for your main module's build.gradle file, you can reference that aar file like so: 
    ```compile 'com.github.gfranks.minimal.notification:com.github.gfranks.minimal.notification-1.0@aar'```

License
-------
Copyright (c) 2015 Garrett Franks. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
