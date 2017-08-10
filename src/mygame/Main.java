package mygame;

import com.jme3.light.DirectionalLight;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener
{
    private static final String TOGGLE_UPDATE = "Toggle Update";
    private Geometry offBox;
    private float angle = 0;
    private ViewPort offView;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public Texture setupOffscreenView(){
        Camera offCamera = new Camera(512, 512);

        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.Black);

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(512, 512, 1);

        //setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(512, 512, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorTexture(offTex);
        
        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // setup framebuffer's scene
        Box boxMesh = new Box(1, 1, 1);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");//assetManager.loadMaterial("Interface/Logo/Logo.j3m");
        offBox = new Geometry("box", boxMesh);
        offBox.setMaterial(material);
        offBox.scale(0.5f);

        // attach the scene to the viewport to be rendered
        offView.attachScene(offBox);
        
        return offTex;
    }
    
    @Override
    public void simpleInitApp()
    {
        Texture offTex = setupOffscreenView();
        
        DirectionalLight dLight = new DirectionalLight();
        dLight.setDirection(new Vector3f(-1.0f,-0.7f,-1.0f));
        
        //Spatial ninjaS = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        Spatial ring = assetManager.loadModel("Models/ring2/ring2.j3o");
        ring.scale(0.1f, 0.1f, 0.1f);
        ring.rotate(1.55f, -0.0f, 0.0f);
        
        Box b = new Box(2, 2, 0);
        Geometry geom = new Geometry("Box", b);

        geom.rotate(0.0f, 0.0f, 0.0f);
        
        Material ringMat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        ring.setMaterial(ringMat);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", offTex);
        //mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        inputManager.addMapping(TOGGLE_UPDATE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, TOGGLE_UPDATE);
        
        rootNode.attachChild(ring);
        rootNode.attachChild(geom);
        
        rootNode.addLight(dLight);
    }

    @Override
    public void simpleUpdate(float tpf){
        Quaternion q = new Quaternion();
        
        if (offView.isEnabled()) {
            angle += tpf;
            angle %= FastMath.TWO_PI;
            q.fromAngles(angle, 0, angle);
            
            offBox.setLocalRotation(q);
            offBox.updateLogicalState(tpf);
            offBox.updateGeometricState();
        }
    }

    @Override
    public void simpleRender(RenderManager rm)
    {
        //TODO: add render code
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(TOGGLE_UPDATE) && isPressed) {
            offView.setEnabled(!offView.isEnabled());
        }
    }
}
