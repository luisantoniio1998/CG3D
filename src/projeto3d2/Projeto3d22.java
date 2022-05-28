package projeto3d2;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;

import cg3d.appearance.Materials;
import cg3d.appearance.TextureAppearence;
import cg3d.shapes.FloorLamp;
import projeto3d2.FiguraRotativa;



import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import static java.lang.Float.max;
import static java.lang.Float.min;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFileChooser;
import javax.swing.Timer;

public class Projeto3d22 extends Applet implements ActionListener, KeyListener, MouseListener {

    private Button go = new Button("Start");
    private Button save = new Button("OffScreen");
    private TransformGroup objTrans;
    private TransformGroup objTrans1;
    private TransformGroup objTrans5;
    private TransformGroup[] objTrans2 = new TransformGroup[30];
    private Transform3D trans = new Transform3D();
    private float sign = 0; // muda a direçao da esfera
    private Timer timer;
    private float linePosition = 0;
    private float xloc = 0.0f;
    BranchGroup objRoot = new BranchGroup(); //objroot será o branch group
    //variaveis das particulas
    private TransformGroup[] objTrans3 = new TransformGroup[40];
    private int[] skyZPosition = new int[40];
    private int[] skyXPosition = new int[40];
    private int[] skyYPosition = new int[40];
    //variaveis dos obstaculos
    private int dificuldade = 20;
    private TransformGroup[] objTrans4 = new TransformGroup[dificuldade];
    private int[] obsZPosition = new int[dificuldade];
    private int[] obsXPosition = new int[dificuldade];
    PickCanvas pc = null;
    
    
    
    private int gameOver = 1;
    
    public void crashVerify(){
        for(int i = 0; i < dificuldade; i++){
            if(obsZPosition[i]*0.1f <= 0f && obsZPosition[i]*0.1f >= -0.6f){
                if(obsXPosition[i]*0.1f <= 0.05f+xloc && obsXPosition[i]*0.1f >= -0.05f+xloc){
                    if(gameOver == 1){
                        gameOver = 1000;
                        System.out.println("FIM DE JOGO");
                    }
                }
            }
        }
    }
    
    
    // PAREDEEEEE //////////////////////
    
    private Shape3D createWall() {
        URL url = getClass().getClassLoader().getResource("images/stone.jpg");
        BufferedImage bi = null;
        try {
          bi = ImageIO.read(url);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bi);
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
        image.getWidth(), image.getHeight());
        texture.setImage(0, image);
        texture.setEnable(true);
        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
        texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
        Appearance appear = new Appearance();
        appear.setTexture(texture);
        QuadArray rect = new QuadArray(4, QuadArray.COORDINATES |
          QuadArray.TEXTURE_COORDINATE_2);
        rect.setCoordinate(0, new Point3d(-2,3,2));
        rect.setCoordinate(1, new Point3d(-2,-3,2));
        rect.setCoordinate(2, new Point3d(-2,-3,-3));
        rect.setCoordinate(3, new Point3d(-2,3,-3));
        rect.setTextureCoordinate(0,0, new TexCoord2f(0f, 0f));
        rect.setTextureCoordinate(0,1, new TexCoord2f(0f, 1f));
        rect.setTextureCoordinate(0,2, new TexCoord2f(1f, 1f));
        rect.setTextureCoordinate(0,3, new TexCoord2f(1f, 0f));
        return new Shape3D(rect, appear);
      }
    
    
    
    public void buildObstacles(){
        Appearance aparencia = new Appearance();
        Color3f cor1 = new Color3f(0.6f, 0.6f, 0.1f);
        Color3f cor2 = new Color3f(0,0,0);
        aparencia.setMaterial(new Material(cor1, cor2, cor1, cor2, 1f));
        Box linha = new Box();
        Transform3D[] pos3 = new Transform3D[40];//adiciona a rua ao segundo transform group
        for (int i = 0; i < dificuldade; i++) {
            objTrans4[i] = new TransformGroup(); // objtrans será o transform group
            linha = new Box(0.05f, 0.03f, 0.3f, aparencia);
            pos3[i] = new Transform3D();
            obsZPosition[i] = ThreadLocalRandom.current().nextInt(-400, -200 + 1);
            obsXPosition[i] = ThreadLocalRandom.current().nextInt(-6, 6 + 1);
            pos3[i].setTranslation(new Vector3f(obsXPosition[i]*0.1f, -0.3f, obsZPosition[i]*0.1f));//posiciona o transform group da rua
            objTrans4[i].addChild(linha);
            objTrans4[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            objTrans4[i].setTransform(pos3[i]);
            objRoot.addChild(objTrans4[i]);
        }
    }
    
    public void moveObstacles(){
        for(int i = 0; i<dificuldade; i++){
            if(obsZPosition[i]>=5f){
                obsZPosition[i] = ThreadLocalRandom.current().nextInt(-400, -200 + 1);
                obsXPosition[i] = ThreadLocalRandom.current().nextInt(-6, 6 + 1);
            }else{
                obsZPosition[i] = obsZPosition[i] + 1;
            }
            trans.setTranslation(new Vector3f(obsXPosition[i]*0.1f, -0.3f, obsZPosition[i]*0.1f));
            objTrans4[i].setTransform(trans);
        }
    }

    public void buildStreet() {
        objTrans1 = new TransformGroup(); // objtrans será o transform group
        Appearance aparencia = new Appearance();
        aparencia.setMaterial(new Material(new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0f, 0f, 0f), new Color3f(0f, 0f, 0f), 10f));
        Box rua = new Box(0.7f, 0.1f, 40f, aparencia);
        Transform3D pos2 = new Transform3D();//adiciona a rua ao segundo transform group
        pos2.setTranslation(new Vector3f(0.0f, -0.5f, 0.0f));//posiciona o transform group da rua
        objTrans1.addChild(rua);
        objTrans1.setTransform(pos2);
        objRoot.addChild(objTrans1);

    }

    public void drawStreetLines() {
        Appearance aparencia = new Appearance();
        Color3f col = new Color3f(1.0f, 1.0f, 1.0f);
        ColoringAttributes ca = new ColoringAttributes(col, ColoringAttributes.NICEST);
        aparencia.setColoringAttributes(ca);
        
        //aparencia.setMaterial(new Material(new Color3f(1.0f, 1.0f, 1.0f), new Color3f(0.1f, 0.7f, 0.7f), new Color3f(0.1f, 0.7f, 0.7f), new Color3f(0.1f, 0.7f, 0.7f), 1f));
        Box linha = new Box();
        Transform3D[] pos3 = new Transform3D[30];//adiciona a rua ao segundo transform group
        for (int i = 0; i < 30; i++) {
            objTrans2[i] = new TransformGroup(); // objtrans será o transform group
            linha = new Box(0.01f, 0.001f, 0.1f, aparencia);
            pos3[i] = new Transform3D();
            pos3[i].setTranslation(new Vector3f(0.0f, -0.4f, 0.4f - 1f * i));//posiciona o transform group da rua
            objTrans2[i].addChild(linha);
            objTrans2[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            objTrans2[i].setTransform(pos3[i]);
            objRoot.addChild(objTrans2[i]);

        }
    }
    
    public void drawSkyEffects(){
        Appearance aparencia = new Appearance();
        aparencia.setMaterial(new Material(new Color3f(1.0f,1.0f,1.0f), new Color3f(0.5f, 0f, 0.4f), new Color3f(0.5f, 0.0f, 0.4f), new Color3f(0.5f, 0.0f, 0.4f), 1f));
        Box linha = new Box();
        Transform3D[] pos3 = new Transform3D[40];//adiciona a rua ao segundo transform group
        for (int i = 0; i < 40; i++) {
            objTrans3[i] = new TransformGroup(); // objtrans será o transform group
            linha = new Box(0.003f, 0.003f, 0.1f, aparencia);
            pos3[i] = new Transform3D();
            skyZPosition[i] = ThreadLocalRandom.current().nextInt(-100, 10 + 1);
            skyYPosition[i] = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
            skyXPosition[i] = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
            pos3[i].setTranslation(new Vector3f(skyXPosition[i]*0.1f, skyYPosition[i]*0.1f, skyZPosition[i]*0.1f));//posiciona o transform group da rua
            objTrans3[i].addChild(linha);
            objTrans3[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            objTrans3[i].setTransform(pos3[i]);
            objRoot.addChild(objTrans3[i]);
        }
    }
    
    public void moveSkyEffects(){
        for(int i = 0; i<40; i++){
            if(skyZPosition[i]>=1.0f){
                skyZPosition[i] = ThreadLocalRandom.current().nextInt(-100, 10 + 1);
                skyYPosition[i] = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
                skyXPosition[i] = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
            }else{
                skyZPosition[i] = skyZPosition[i] + 1;
            }
            trans.setTranslation(new Vector3f(skyXPosition[i]*0.1f, skyYPosition[i]*0.1f, skyZPosition[i]*0.1f));
            objTrans3[i].setTransform(trans);
        }
    }

    public void moveStreetLines() {
            if (linePosition >= 1.0f) {
                linePosition = 0;
            }else{
                linePosition = linePosition + 0.1f;
            }
        for (int i = 0; i < 30; i++) {
            trans.setTranslation(new Vector3f(0.0f, -0.4f, linePosition + 0.4f - 1f * i));
            objTrans2[i].setTransform(trans);

        }

    }

    public void buildCar() {
        // Create the root of the branch graph
        Appearance aparencia = new Appearance();
        Appearance aparencia1 = new Appearance();
        aparencia.setMaterial(new Material(new Color3f(0.0f, 0.0f, 0.6f), new Color3f(0.0f, 0.0f, 0.6f), new Color3f(1.0f, 1.0f, 0.6f), new Color3f(1.0f, 0f, 0.6f), 10f));
        objTrans = new TransformGroup(); // objtrans será o transform group
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // Create a simple shape leaf node, add it to the scene graph.
        Box sphere = new Box(0.05f, 0.03f, 0.3f, aparencia); // cria uma esfera

        aparencia1.setMaterial(new Material(new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0f, 0f, 0.0f), new Color3f(0f, 0f, 0f), 10f));
        Box sphere1 = new Box(0.04f, 0.01f, 0.31f, aparencia1);

        Box sphere2 = new Box(0.0501f, 0.01f, 0.27f, aparencia1);

        Transform3D pos1 = new Transform3D();//cria o responsavel pelas posicoes

        pos1.setTranslation(new Vector3f(0.0f, -0.3f, 0.2f)); //cria um vetor para transladar pos1

        objTrans.setTransform(pos1); //aplica o vetor criado na linha de cima em objTrans, ou seja todos os objetos contidos nesse transformGroup serao tranladados

        objTrans.addChild(sphere1); // a esfera criada passa a fazer parte do transformGroup
        objTrans.addChild(sphere);
        objTrans.addChild(sphere2);
        objRoot.addChild(objTrans); //O transformGroup passa a fazer parte do BranchGroup

    }


    public BranchGroup createSceneGraph() {
    	
    	// Figura Rotativa //////////////////////////////////////////////////////// 
    	 objRoot = new BranchGroup();
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(spin);

		// object
		Appearance ap = new Appearance();
		ap.setMaterial(new Material());
		Shape3D shape = new FiguraRotativa();
		shape.setAppearance(ap);

		Transform3D tr = new Transform3D();
		tr.setScale(0.15);
		tr.setTranslation(new Vector3f(-2.1f,0.28f,0f));
		TransformGroup tg = new TransformGroup(tr);
		spin.addChild(tg);
		tg.addChild(shape);
		
		
		//PAREDE ////////////////////////////////////////////
		Shape3D wall = createWall();
		tg.addChild(wall);
		
		Point3f lightPos = new Point3f(10f,3f,1f);
		GeometryArray geom = (GeometryArray)shape.getGeometry();
		
		
		// SHADOW ////////////////////////////////////////////
	    GeometryArray shadow = createShadow(geom, lightPos, new Point3f(-2f, 3f, 1f));
	    ap = new Appearance();
	    ColoringAttributes colorAttr = new ColoringAttributes(0.1f, 0.1f, 0.1f, 
	    ColoringAttributes.FASTEST);
	    ap.setColoringAttributes(colorAttr);
	    TransparencyAttributes transAttr = new TransparencyAttributes(
	    TransparencyAttributes.BLENDED,0.35f);
	    ap.setTransparencyAttributes(transAttr);
	    PolygonAttributes polyAttr = new PolygonAttributes();
	    polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
	    ap.setPolygonAttributes(polyAttr);
	    shape = new Shape3D(shadow, ap);
	    tg.addChild(shape);

		Alpha alpha = new Alpha(-1, 4000);
		RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
		BoundingSphere bounds1 = new BoundingSphere();
		rotator.setSchedulingBounds(bounds1);
		spin.addChild(rotator);
		
		
    	//TEXTO 3D ///////////////////////////////////////////////

    	Appearance text3dap = new Appearance();
    	text3dap.setMaterial(new Material());
    	Font3D font = new Font3D(new Font("SansSerif", Font.PLAIN, 1), new FontExtrusion());
    	Text3D text = new Text3D(font, "PROJETO 3D");
    	Shape3D shape3dtext = new Shape3D(text, text3dap);

    	Transform3D textt = new Transform3D();
    	textt.setScale(0.2);
    	textt.setTranslation(new Vector3f(-0.6f, 0.28f, 0f));
    	TransformGroup ttext = new TransformGroup(textt);
    	objRoot.addChild(ttext);
    	ttext.addChild(shape3dtext);

    	// The tg that is parent of the table, must have permissions to be part of the 
    	// picking result and to read and write its geometric transformation
    	ttext.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    	ttext.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    	ttext.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    	
    	//Transfrom Group SOUND
    	TransformGroup objTrans = new TransformGroup(trans);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRoot.addChild(objTrans);
    	
        
        // SOUND 3D 
        PointSound sound = new PointSound();
        URL url = this.getClass().getClassLoader().getResource("images/sample3.au");
        MediaContainer mc = new MediaContainer(url);
        sound.setSoundData(mc);
        sound.setLoop(Sound.INFINITE_LOOPS);
        sound.setInitialGain(1f);
        sound.setEnable(true);
        float[] distances = {1f, 20f};
        float[] gains = {1f, 0.001f};
        sound.setDistanceGain(distances, gains);
        BoundingSphere soundBounds =
        new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        sound.setSchedulingBounds(soundBounds);
        objTrans.addChild(sound);
        
        
        /*
        //BACKGROUND
        Background background = new Background(new Color3f(0.1f, 0f, 0.2f));
        BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 100000);
        background.setApplicationBounds(sphere);
        objRoot.addChild(background);*/
        
      // BACKGROUND ////////////////////////
        url = getClass().getClassLoader().getResource("images/Split_Sky.jpg");
        BufferedImage bi = null;
        try {
          bi = ImageIO.read(url);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bi);
        Background background = new Background(image);
        BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 100.0);
        background.setApplicationBounds(sphere);
        objRoot.addChild(background);
        
     
        this.drawStreetLines();
        this.buildCar();
        this.buildStreet();
        this.drawSkyEffects();
        this.buildObstacles();
        BoundingSphere bounds2
                = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f light1Color = new Color3f(Color.white);

        Vector3f light1Direction = new Vector3f(0.0f, -7.0f, -10.0f);

        DirectionalLight light1
                = new DirectionalLight(light1Color, light1Direction);

        light1.setInfluencingBounds(bounds2);

        objRoot.addChild(light1); //cria uma luz direcional e adiciona ao branch group

        // Set up the ambient light
        Color3f ambientColor = new Color3f(1.0f, 1.0f, 0.0f);

        AmbientLight ambientLightNode = new AmbientLight(ambientColor);

        ambientLightNode.setInfluencingBounds(bounds2);

        objRoot.addChild(ambientLightNode);

        return objRoot;

    }
    
    
    /// SHADOWW /////////////////////////////////////////////////////
    
    private GeometryArray createShadow(GeometryArray ga, Point3f light, Point3f plane) {
        
    	// A geometria do polígono que simula a sombra vai ser criado com IndexedTriangleArray
    	// pelo que a geometria do objeto é transformada para esse tipo, de modo a depois extrair
    	// caracteristicas da geometria do objeto para construir a geometria da sombra, como por
    	// exemplo, o número de vértices.  
    	
    	// The geometry of the polygon that simulates the shadow will be created with IndexedTriangleArray
    	// so the geometry of the object is transformed for this type, in order to later extract
    	// characteristics of object geometry to construct shadow geometry, such as the number of vertices.
    	GeometryInfo gi = new GeometryInfo(ga);
        gi.convertToIndexedTriangles();
        IndexedTriangleArray ita = (IndexedTriangleArray)gi.getIndexedGeometryArray();
        
        // Determinar um vetor entre a fonte de luz e o plano de projeção da sombra para obter 
        // a distância entre os dois. 
        
        // Determine a vector between the light source and the shadow projection plane to obtain
        // the distance between the two.
        Vector3f v = new Vector3f();
        v.sub(plane, light);
        
        // Construir a matriz da transformação de projeção para a forma standard.
        
        // Construct the matrix of the projection transformation to the standard form.
        double[] mat = new double[16];
        for (int i = 0; i < 16; i++) {
          mat[i] = 0;
        }
        mat[0] = 1;
        mat[5] = 1;
        mat[10] = 1-0.001;
        mat[14] = -1/v.length();    
        Transform3D proj = new Transform3D();
        proj.set(mat);
        
        // Acrescentar a transformação U e U-1 dada por lookat para transformar casos genéricos 
        // na forma standard.
        
        // Add the U and U-1 transformation given by lookat to transform generic cases
        // into standard form.
        Transform3D u = new Transform3D();
        u.lookAt(new Point3d(light), new Point3d(plane), new Vector3d(0,1,0));
        proj.mul(u);
        Transform3D tr = new Transform3D();
        u.invert();
        tr.mul(u, proj);
        
        // Criar a gemetria do polígono da sombra com o mesmo número de vértices e índices da
        // geometria do objeto.
        
        // Create the polygon of the shadow with the same number of vertices and indexes of the
        // geometry of the object.
        int n = ita.getVertexCount();
        int count = ita.getIndexCount();
        IndexedTriangleArray shadow = new IndexedTriangleArray(n, 
          GeometryArray.COORDINATES, count);
        
        // Calcular as coordenadas dos vértices do polígono da sombra aplicando a transformação 
        // criada anteriormente aos vértices do objeto. 
        
        // Calculate the coordinates of the vertices of the shadow polygon by applying the transformation
        // created before, to the vertices of the object.
        
        for (int i = 0; i < n; i++) {
          Point3d p = new Point3d();
          ga.getCoordinate(i, p);
          Vector4d v4 = new Vector4d(p);
          v4.w = 1;
          tr.transform(v4);
          Point4d p4 = new Point4d(v4);
          p.project(p4);
          shadow.setCoordinate(i, p);
        }
        
        // Copiar os índices de coordenadas dos vértices.
        // Notar que os índices são iguais, o que difere são as coordenadas dos vértices que são
        // transformados pela transformação composta.
        
        // Copy the coordinate indices of the vertices.
        // Note that the indices are equal, what differs are the coordinates of the vertices that are
        // transformed by the composite transformation.
        int[] indices = new int[count];
        ita.getCoordinateIndices(0, indices);
        shadow.setCoordinateIndices(0, indices);
        return shadow;
      }
    
    private Canvas3D c;
    private Canvas3D offScreenCanvas;
    private View view;

    public Projeto3d22() {
    	
    	
        setLayout(new BorderLayout());

        GraphicsConfiguration config
                = SimpleUniverse.getPreferredConfiguration();

        c = new Canvas3D(config);

        add("Center", c);

        c.addKeyListener(this);
        c.addMouseListener(this);

        timer = new Timer(10, this);

        //timer.start();
        Panel p = new Panel();
        SimpleUniverse u = new SimpleUniverse(c);
        p.add(go);
        
        
        // create off screen canvas
        view = u.getViewer().getView();
        offScreenCanvas = new Canvas3D(config, true);
        Screen3D sOn = c.getScreen3D();
        Screen3D sOff = offScreenCanvas.getScreen3D();
        Dimension dim = sOn.getSize();
        sOff.setSize(dim);
        sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
        sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
        Point loc = c.getLocationOnScreen();
        offScreenCanvas.setOffScreenLocation(loc);
        save.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent ev) {
                BufferedImage bi = capture();
                save(bi);
              }
        });

        p.add(save);

        add("North", p);

        go.addActionListener(this);

        go.addKeyListener(this);

        // Create a simple scene and attach it to the virtual universe
        BranchGroup scene = createSceneGraph();


        
        //AudioDevice audioDev = su.getViewer().createAudioDevice();
        AudioDevice audioDev = new JavaSoundMixer(u.getViewer().getPhysicalEnvironment());
    	audioDev.initialize();

        u.getViewingPlatform().setNominalViewingTransform();

        u.addBranchGraph(scene);
        
    	pc = new PickCanvas(c, scene);
		pc.setMode(PickTool.GEOMETRY);

    }
    
    	//Guardar imagem OffScreen
    
    public BufferedImage capture() {
        // render off screen image
        Dimension dim = c.getSize();
        view.stopView();
        view.addCanvas3D(offScreenCanvas);
        BufferedImage bImage =
        new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
        ImageComponent2D buffer =
        new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage);
        offScreenCanvas.setOffScreenBuffer(buffer);
        view.startView();
        offScreenCanvas.renderOffScreenBuffer();
        offScreenCanvas.waitForOffScreenRendering();
        bImage = offScreenCanvas.getOffScreenBuffer().getImage();
        view.removeCanvas3D(offScreenCanvas);
        return bImage;
      }

    public void save(BufferedImage bImage) {
        // save image to file
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
          File oFile = chooser.getSelectedFile();
          try {
            ImageIO.write(bImage, "jpeg", oFile);
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    
    public void keyPressed(KeyEvent e) {

        //Invoked when a key has been pressed.
        if (e.getKeyChar() == 'd') {
            sign = 1;
        }

        if (e.getKeyChar() == 'a') {
            sign = -1;
        }

    }

    public void keyReleased(KeyEvent e) {

        if (e.getKeyChar() == 'd') {
            sign = 0;
        }

        if (e.getKeyChar() == 'a') {
            sign = 0;
        }

        // Invoked when a key has been released.
    }

    public void keyTyped(KeyEvent e) {

        //Invoked when a key has been typed.
    }

    public void actionPerformed(ActionEvent e) {
        // start timer when button is pressed
        if (e.getSource() == go) {

            if (!timer.isRunning()) {

                timer.start();

            }

        } else {

            xloc += .01 * sign;
            if(xloc >= .6){
                xloc -= 0.01;
            }
            if(xloc <= -.6){
                xloc += 0.01;
            }
            this.moveStreetLines();
            this.moveSkyEffects();
            this.moveObstacles();
            this.crashVerify();
            trans.setTranslation(new Vector3f(xloc, -0.3f, gameOver*0.2f));
            objTrans.setTransform(trans);

        }

    }

    public static void main(String[] args) {

        System.out.println("Program Started");

        Projeto3d22 bb = new Projeto3d22();

        bb.addKeyListener(bb);

        MainFrame mf = new MainFrame(bb, 640, 480);
        
    }

   
	@Override
	public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);
		
		PickResult result = pc.pickClosest();
		
		if(result != null) {
		    System.out.println("1");
		    
			TransformGroup tg = (TransformGroup) result.getNode(PickResult.TRANSFORM_GROUP);
			if(tg != null) {
			
				System.out.println("2");
				Transform3D tr = new Transform3D();
				tg.getTransform(tr);
				
				Transform3D rot = new Transform3D();
				rot.rotY(Math.PI/4);
				tr.mul(rot);
				//rot.mul(tr);
				
				tg.setTransform(tr);
			}
		}
			
}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}


