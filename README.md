# GfxAssert
A set of possibley useful Java (unit)test tools for geometric shapes and images using hamcrest matchers.

## Maven
    <dependencies>
		<dependency>
			<groupId>net.mabboud.gfxassert</groupId>
			<artifactId>GfxAssert</artifactId>
            <version>1.0.4</version>
		</dependency>
    </dependencies>

    <repositories>
		<repository>
			<id>jcenter</id>
			<url>http://jcenter.bintray.com </url>
		</repository>
    </repositories>


## Basic Usage
##### Static import all shape matchers
```java
    import static org.mabb.gfxassert.geom.ShapeSubsetDescriptor.*;
    import static org.mabb.gfxassert.GfxAssertMatchers.*;
```

#### Asserting that a color is in a target area of an image
```java
    BufferedImage image = ImageIO.read(new File("test-image-contains-colors.png"));

    Assert.assertThat(image, containsColor(Color.red).in(bottom(50).percent()));
```

#### Asserting that a target shape is in a particular area of a containing shape
```java
    Rectangle2D.Double containingRect = new Rectangle2D.Double(0, 0, 100, 100);
    Rectangle2D.Double targetRect = new Rectangle2D.Double(0, 55, 30, 30);

    Assert.assertThat(containingRect, containsShape(targetRect).in(bottomArea()));
```