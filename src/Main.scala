import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TmxMapLoader}
import com.badlogic.gdx.math.Vector2

import java.util
import java.util.{Map, TreeMap}

object Main {
  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication (20 * 32,21 * 32){
  // Bookmark: Tiled map managers
  private var tiledMap: TiledMap = null
  private var tiledMapRenderer: TiledMapRenderer = null

  // Bookmark: Camera manipulation
  private var zoom: Float = 0
  private var cameraPosition = new Vector2(0,0)

  // Bookmark: Hero settings
  private var hero: Hero = null

  // Bookmark: Key management
  private val keyStatus: util.Map[Integer, Boolean] = new util.TreeMap[Integer, Boolean]



  override def onInit(): Unit = {
    // Bookmark: Set zoom
    zoom = 0.5f

    // Bookmark: Create hero
    hero = new Hero

    // Bookmark: Create map
    setTitle("Traffic Rider")
    tiledMap = new TmxMapLoader().load("data/Tiled/highway.tmx")
    tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap)

    // Bookmark: Initialize keys
    keyStatus.put(Input.Keys.W, false) // up
    keyStatus.put(Input.Keys.S, false) // down
    keyStatus.put(Input.Keys.A, false) // right
    keyStatus.put(Input.Keys.D, false) // left
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    g.zoom(zoom)

    tiledMapRenderer.setView(g.getCamera)
    tiledMapRenderer.render()

    hero.draw(g)

    g.drawSchoolLogo()
    g.drawFPS()
  }
}