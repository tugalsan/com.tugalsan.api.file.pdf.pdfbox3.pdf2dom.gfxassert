package org.mabb.gfxassert;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mabb.gfxassert.ShapeSearchArea.AreaDescriptor.SearchType.*;

public class ShapeSearchArea {
    protected List<AreaDescriptor> searchAreas = new ArrayList<AreaDescriptor>();
    protected String description = "Area";

    private static final String TOP_AREA_DESCRIPTION = "Top Area";
    private static final String BOTTOM_AREA_DESCRIPTION = "Bottom Area";
    private static final String RIGHT_AREA_DESCRIPTION = "Right Area";
    private static final String LEFT_AREA_DESCRIPTION = "Left Area";
    private static final String ALL_AREA_DESCRIPTION = "All";

    private static final String TOP_RIGHT_DESCRIPTION = "Top Right Area";
    private static final String TOP_LEFT_DESCRIPTION = "Top Left Area";
    private static final String BOTTOM_RIGHT_DESCRIPTION = "Bottom Right Area";
    private static final String BOTTOM_LEFT_DESCRIPTION = "Bottom Left Area";

    public ShapeSearchArea(AreaDescriptor... shapes) {
        searchAreas.addAll(Arrays.asList(shapes));
    }

    public static ShapeSearchArea topArea() {
        return top(50).percent();
    }

    public static ShapeSearchArea bottomArea() {
        return bottom(50).percent();
    }

    public static ShapeSearchArea leftArea() {
        return left(50).percent();
    }

    public static ShapeSearchArea rightArea() {
        return right(50).percent();
    }

    public static ShapeSearchArea topRightArea() {
        ShapeSearchArea area = new ShapeSearchArea();
        area.add(new PercentArea(50, TOP));
        area.add(new PercentArea(50, RIGHT));
        area.description = TOP_RIGHT_DESCRIPTION;

        return area;
    }

    public static ShapeSearchArea topLeftArea() {
        ShapeSearchArea area = new ShapeSearchArea();
        area.add(new PercentArea(50, TOP));
        area.add(new PercentArea(50, LEFT));
        area.description = TOP_LEFT_DESCRIPTION;

        return area;
    }

    public static ShapeSearchArea bottomRightArea() {
        ShapeSearchArea area = new ShapeSearchArea();
        area.add(new PercentArea(50, BOTTOM));
        area.add(new PercentArea(50, RIGHT));
        area.description = BOTTOM_RIGHT_DESCRIPTION;

        return area;
    }

    public static ShapeSearchArea bottomLeftArea() {
        ShapeSearchArea area = new ShapeSearchArea();
        area.add(new PercentArea(50, BOTTOM));
        area.add(new PercentArea(50, LEFT));
        area.description = BOTTOM_LEFT_DESCRIPTION;

        return area;
    }

    public static ShapeSearchArea all() {
        ShapeSearchArea area = new ShapeSearchArea();
        area.add(new PercentArea(100, ALL));
        area.description = ALL_AREA_DESCRIPTION;

        return area;
    }

    public List<Rectangle2D> getToScale(Shape container) {
        List<Rectangle2D> scaledAreas = new ArrayList<Rectangle2D>(searchAreas.size());

        for (AreaDescriptor area : searchAreas)
            scaledAreas.add(area.applyForContainer(container).getBounds2D());

        return scaledAreas;
    }

    protected void add(AreaDescriptor shape) {
        searchAreas.add(shape);
    }

    public String toString() {
        return description;
    }

    public boolean contains(Shape target, Shape container) {
        Rectangle2D targetRect = target.getBounds2D();

        for (Rectangle2D searchShapeOn : getToScale(container)) {
            if (!searchShapeOn.contains(targetRect))
                return false;
        }

        return true;
    }

    public static AreaDescriptor center(int num) {
        return new PercentArea(num, CENTER);
    }

    public static AreaDescriptor right(int num) {
        return new PercentArea(num, RIGHT);
    }

    public static AreaDescriptor left(int num) {
        return new PercentArea(num, LEFT);
    }

    public static AreaDescriptor top(int num) {
        return new PercentArea(num, TOP);
    }

    public static AreaDescriptor bottom(int num) {
        return new PercentArea(num, BOTTOM);
    }

    /**
     * Seperate class in order to force .percent() or .pixels() syntax after a bottom(20)
     */
    public abstract static class AreaDescriptor {
        protected enum SearchType {
            TOP, BOTTOM, RIGHT, LEFT, CENTER, ALL
        }

        protected double number;
        protected final SearchType searchArea;

        public AreaDescriptor(double num, SearchType bottom) {
            this.number = num;
            this.searchArea = bottom;
        }

        public ShapeSearchArea percent() {
            return new ShapeSearchArea(new PercentArea(number, searchArea));
        }

        public ShapeSearchArea pixels() {
            return new ShapeSearchArea(new PixelArea(number, searchArea));
        }

        public abstract Shape applyForContainer(Shape container);
    }

    public static class PercentArea extends AreaDescriptor {
        public PercentArea(double num, SearchType type) {
            super(num, type);
        }

        public Shape applyForContainer(Shape container) {
            Rectangle2D.Double rect;

            double pct = number / 100.0;
            switch (searchArea) {
                case TOP:
                    rect = new Rectangle2D.Double(0, 0, 1, pct);
                    break;
                case BOTTOM:
                    rect = new Rectangle2D.Double(0, 1 - pct, 1, pct);
                    break;
                case RIGHT:
                    rect = new Rectangle2D.Double(1 - pct, 0, pct, 1);
                    break;
                case LEFT:
                    rect = new Rectangle2D.Double(0, 0, pct, 1);
                    break;
                case CENTER:
                    rect = new Rectangle2D.Double(0.5 - (pct / 2), 0.5 - (pct / 2), pct, pct);
                    break;
                default:
                case ALL:
                    rect = new Rectangle2D.Double(0, 0, 1, 1);
                    break;
            }

            return scaleTo(rect, container);
        }

        private Shape scaleTo(Shape search, Shape scaleToArea) {
            Rectangle2D scaleTo = scaleToArea.getBounds2D();
            Rectangle2D searchBounds = search.getBounds2D();

            Rectangle2D.Double scaledAreaOn = new Rectangle2D.Double();
            scaledAreaOn.height = searchBounds.getHeight() * scaleTo.getHeight();
            scaledAreaOn.width = searchBounds.getWidth() * scaleTo.getWidth();
            scaledAreaOn.x = searchBounds.getX() * scaleTo.getWidth() + scaleTo.getX();
            scaledAreaOn.y = searchBounds.getY() * scaleTo.getHeight() + scaleTo.getY();

            return scaledAreaOn;
        }
    }

    public static class PixelArea extends AreaDescriptor {
        public PixelArea(double num, SearchType type) {
            super(num, type);
        }

        public Shape applyForContainer(Shape container) {
            Rectangle2D rect;
            Rectangle2D scaleTo = container.getBounds2D();

            double pixels = number;
            switch (searchArea) {
                case TOP:
                    rect = new Rectangle2D.Double(0, 0, scaleTo.getWidth(), pixels);
                    break;
                case BOTTOM:
                    rect = new Rectangle2D.Double(0, scaleTo.getHeight() + scaleTo.getY() - pixels, scaleTo.getWidth(), pixels);
                    break;
                case RIGHT:
                    rect = new Rectangle2D.Double(scaleTo.getWidth() + scaleTo.getX() - pixels, 0, pixels, 1);
                    break;
                case LEFT:
                    rect = new Rectangle2D.Double(0, 0, pixels, scaleTo.getHeight());
                    break;
                case CENTER:
                    rect = new Rectangle2D.Double(
                            scaleTo.getHeight() + scaleTo.getY() - (pixels / 2),
                            scaleTo.getWidth() + scaleTo.getX() - (pixels / 2),
                            pixels, pixels);
                    break;
                default:
                case ALL:
                    rect = scaleTo;
                    break;
            }

            return rect;
        }
    }
}
