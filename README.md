# AndroidSimpleMVP
A simple App to demonstrate the design of Model-View-Presenter in Android.

Inspired by this post: 

http://philosophicalhacker.com/2015/05/08/how-to-make-our-android-apps-unit-testable-pt-2/

The goal is to move business logics out of Android Components such as Activity, Fragment, Service and place they in POJO java classes, which have very little dependency on Android SDK (Only classes that are mockable such as: MockCursor, MockApplication shall be passed into them), and these classes are called Presenters. 

As a result, the Presenter classes are Unit Testable and less error prone. 

The <b>TestCase class MainActPresenterTest</b> is included in the project to demonstrate how to test against the MainActPresenter class
