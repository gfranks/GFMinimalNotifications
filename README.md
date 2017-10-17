GFMinimalNotification 2.0
===========

GFMinimalNotifications was inspired by JFMinimalNotifications created by Jeremy Fox (https://github.com/atljeremy/JFMinimalNotifications) and Google's Snackbar

This is an Android controller for presenting a beautiful notification that is highly configurable and works for both phone and tablet. GFMinimalNotification is only available for API levels 16+.
because well, fragmentation is a bad thing. 

If you would like to contribute or have any issues, please use the issue tracker or email me directly at lgfz71@gmail.com

What It Looks Like:
------------------

![Examples](/resources/screenshot_error.png?raw=true) ![Examples](/resources/screenshot_default.png?raw=true) ![Examples](/resources/screenshot_warning.png?raw=true)

### Update 2.1
This notification can now use a custom view to be shown within the notification. Padding is already applied to the container,
so you may ignore this in your custom layouts. If you set a background color/drawable on the custom view, you will see an alpha change
affect this and the bounds will make it appear oddly. To mediate this, please use the `setCustomBackgroundColor(int)` method to apply your
own color.

### Update 2.1.1
Add ability to update the maximum number of lines displayed in the notification

### Update 2.2.0
Resolved a crash when attempting to resolve themed attributes

How To Use It:
-------------

### Basic Example

```java

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // passing any view here works. It will find a suitable parent to display this, however, it will be the first ViewGroup
        // it finds. 
        GFMinimalNotification.make(rootView, "Some text", GFMinimalNotification.LENGTH_LONG, GFMinimalNotification.TYPE_DEFAULT).show();
    }
}
```

### Constructors / Options

```java

// Constructors
public static GFMinimalNotification make(View view);

public static GFMinimalNotification make(View view, CharSequence text,
                           int duration);
   
public static GFMinimalNotification make(View view, int resId, int duration);

public static GFMinimalNotification make(View view, CharSequence text,
                           int duration, int type);
                                         
public static GFMinimalNotification make(View view, int resId, int duration,
                           int type);

public static GFMinimalNotification make(View view, int customViewResId);

public static GFMinimalNotification make(View view, View customView);
   
/**
 * Set the action to be displayed. Doing so removes the action image, if any
 */
public GFMinimalNotification setAction(int resId, OnActionClickListener listener);
public GFMinimalNotification setAction(CharSequence text, OnActionClickListener listener);

/**
 * Sets the text color of the action specified in
 */
public GFMinimalNotification setActionTextColor(ColorStateList colors);
public GFMinimalNotification setActionTextColor(int color);

/**
 * Set the action drawable resource to be displayed. Doing so removes the action text, if any
 */
public GFMinimalNotification setActionImage(int resId, OnActionClickListener listener);
public GFMinimalNotification setActionImage(Drawable drawable, OnActionClickListener listener);

/**
 * Set the helper drawable resource to be displayed
 */
public GFMinimalNotification setHelperImage(int resId);
public GFMinimalNotification setHelperImage(Drawable drawable);

/**
 * Update the text of the notification
 */
public GFMinimalNotification setText(CharSequence message);
public GFMinimalNotification setText(int resId);

/**
 * Update the text appearance in the notification
 */
public GFMinimalNotification setTextAppearance(int resId);

/**
 * Update the maximum number of lines displayed in the notification
 */
public GFMinimalNotification setMaxLines(int maxLines);

/**
 * Supply your own custom view to be shown in the notification
 */
public GFMinimalNotification setCustomView(int customViewResId);
public GFMinimalNotification setCustomView(View customView);

/**
 * Update how long the notification is displayed
 */
public GFMinimalNotification setDuration(int duration);

/**
 * Set the direction the notification should animate in from. (DIRECTION_TOP or DIRECTION_BOTTOM)
 */
public GFMinimalNotification setDirection(int direction);

/**
 * Update the type of notification. (TYPE_DEFAULT, TYPE_ERROR, or TYPE_WARNING)
 */
public GFMinimalNotification setType(int type);

/**
 * Set your own background color on the notification
 */
public GFMinimalNotification setCustomBackgroundColor(int customBackgroundColor);

/**
 * Set your own tint colors used to tint icons (helper and action)
 */
public GFMinimalNotification setCustomIconTintColor(int customIconTintColor);
```

### Theming / Types
```java
// Apply global theme for default type colors and text appearance
<style name="YourAppTheme" parent="Theme.AppCompat">
    ...

    // Overrides the default type background color
    <item name="gf_notification_type_default">@color/my_default_color</item>

    // Overrides the error type background color
    <item name="gf_notification_type_error">@color/my_error_color</item>

    // Overrides the warning type background color
    <item name="gf_notification_type_warning">@color/my_warning_color</item>

    // Set your own text appearance
    <item name="gf_notification_textAppearance">@style/my_text_appearance</item>

    // Overrides the maxLines of notification (defaults to 2)
    <item name="gf_notification_maxLines">5</item>
    
    // Overrides the default direction to display the notification from
    <item name="gf_notification_direction">top|bottom</item>
    
</style>
```

```java
// Available Types
/**
 * Show the GFMinimalNotification with the default type settings. This means that the GFMinimalNotification
 * will be have a default background and text color.
 *
 * @see #setType
 */
TYPE_DEFAULT;

/**
 * Show the GFMinimalNotification with the error type settings. This means that the GFMinimalNotification
 * will be have a error background and text color.
 *
 * @see #setType
 */
TYPE_ERROR;

/**
 * Show the GFMinimalNotification with the warning type settings. This means that the GFMinimalNotification
 * will be have a warning background and text color.
 *
 * @see #setType
 */
TYPE_WARNING;
```

```java
// Available Directions
/**
 * Show the notification from the top of the container layout found
 *
 * @see #setDirection
 */
DIRECTION_TOP;

/**
 * Show the notification from the bottom of the container layout found
 *
 * @see #setDirection
 */
DIRECTION_BOTTOM;
```

Please see the sample project included in this repo for an example of how to use this notification.


Callback Methods:
----------------
```java
/************
 * Callback *
 ************/
/**
 * Called when the given {@link GFMinimalNotification} is visible.
 *
 * @param notification The notification which is now visible.
 * @see GFMinimalNotification#show()
 */
void onShown(GFMinimalNotification notification) 

/**
 * Called when the given {@link GFMinimalNotification} has been dismissed, either through a time-out,
 * having been manually dismissed, or an action being clicked.
 *
 * @param notification The notification which has been dismissed.
 * @param event The event which caused the dismissal. One of either:
 *              {@link #DISMISS_EVENT_SWIPE}, {@link #DISMISS_EVENT_ACTION},
 *              {@link #DISMISS_EVENT_TIMEOUT}, {@link #DISMISS_EVENT_MANUAL} or
 *              {@link #DISMISS_EVENT_CONSECUTIVE}.
 *
 * @see GFMinimalNotification#dismiss()
 */
void onDismissed(GFMinimalNotification notification, @GFMinimalNotification.Callback.DismissEvent int event)


/*************************
 * OnActionClickListener *
 *************************/
/**
 * Called when the given {@link GFMinimalNotification} action view (text or image) has been clicked.
 * Will dismiss if returned true
 *
 * @param notification The notification which has received the action click (text or image)
 * @return true if you wish to dismiss the notification immediately or false to allow it to dismiss normally
 */
boolean onActionClick(GFMinimalNotification notification)

(NOTE: If you supply a custom view, do not expect to receive this callback as this view and it's listener will have been removed)
```

Features Coming:
------------

- Additional TYPEs (i.e. Success, Info - removed for v2). There is no intention to bring those back, but additional ones may be added. 

Installation:
------------

### Gradle/JitPack

- Add JitPack to your top-level build.gradle file
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
- Add GFMinimalNotifications to your module's build.gradle file
```
dependencies {
     api 'com.github.gfranks:GFMinimalNotifications:<version>'
}
```

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
