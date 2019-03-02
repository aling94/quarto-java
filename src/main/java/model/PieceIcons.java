package model;

import model.Attribute.Att;
import static model.Attribute.Att.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Separate class for handling IO loading of images (test package doesn't seem to like it when I include this in a
 * class that's being tested).
 */
public class PieceIcons
{
    /**
     * Helper function for constructing an ImageIcon matching the given attribute list.
     * @param atts Set
     * @return ImageIcon
     */
    public static ImageIcon getIcon(Set<Att> atts)
    {
        Set<Att> searchKey = new HashSet<>(atts);
        searchKey.removeAll(FILTER);
        BufferedImage base = BASES.get(searchKey);
        if(base == null) base = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        BufferedImage icon = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = icon.getGraphics();
        g.drawImage(base, 0,0, null);
        drawAddons(g, atts);
        ImageIcon out = new ImageIcon(icon);
        g.dispose();
        return out;
    }

    /**
     * Helper function for painting add-on attributes (TOP, BAR, SLASH)
     * @param g Graphics
     * @param atts Set
     */
    private static void drawAddons(Graphics g, Set<Att> atts)
    {
        Set<Att> addons = new LinkedHashSet<>(atts);
        addons.retainAll(FILTER);
        for(Att a : addons)
        {
            BufferedImage overlay = ADD_ONS.get(a);
            if(overlay != null)
                g.drawImage(overlay, 0, 0, null);
        }
    }

    private static final Set<Att> FILTER = new HashSet<Att>(Arrays.asList(new Att[]{HLW, SLD, FORW, BACK, FORW, VERT, DASH}));
    private static HashMap<Set<Att>, BufferedImage> BASES = new HashMap<>();
    private static HashMap<Att, BufferedImage> ADD_ONS = new HashMap<>();

    /**
     * Helper for doing Image IO.
     * @param path String
     * @return BufferedImage
     * @throws IOException
     */
    private static BufferedImage loadImg(String path) throws IOException
    {
        return ImageIO.read(PieceIcons.class.getResource("bases/" + path));
    }

    /**
     * Helper for adding an image into the lookup table.
     * @param atts Att[]
     * @param path String
     * @throws IOException
     */
    private static void PUT_IMG(Att[] atts, String path) throws IOException
    {
        BASES.put(new HashSet<Att>(Arrays.asList(atts)), loadImg(path));
    }

    /**
     * Initializes the lookup tables that are used to construct Icons.
     */
    static
    {
        try
        {
            PUT_IMG(new Att[]{BIG, BWN, SQR}, "big_bwn_sqr.png");
            PUT_IMG(new Att[]{BIG, YLW, SQR}, "big_ylw_sqr.png");
            PUT_IMG(new Att[]{BIG, BWN, CIR}, "big_bwn_cir.png");
            PUT_IMG(new Att[]{BIG, YLW, CIR}, "big_ylw_cir.png");
            PUT_IMG(new Att[]{SML, BWN, SQR}, "sml_bwn_sqr.png");
            PUT_IMG(new Att[]{SML, YLW, SQR}, "sml_ylw_sqr.png");
            PUT_IMG(new Att[]{SML, BWN, CIR}, "sml_bwn_cir.png");
            PUT_IMG(new Att[]{SML, YLW, CIR}, "sml_ylw_cir.png");

            ADD_ONS.put(HLW , loadImg("hole.png"));
            ADD_ONS.put(FORW, loadImg("forw.png"));
            ADD_ONS.put(BACK, loadImg("back.png"));
            ADD_ONS.put(VERT, loadImg("vert.png"));
            ADD_ONS.put(DASH, loadImg("dash.png"));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
