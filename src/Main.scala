import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTileLayer, TmxMapLoader}
import com.badlogic.gdx.math.Vector2

import java.util
import java.util.{Map, TreeMap}
import scala.annotation.meta.param

object Main {
  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication (20 * 32,21 * 32){
  // Bookmark: Tiled map managers
  private var tiledMap: TiledMap = null
  private var tiledMapRenderer: TiledMapRenderer = null
  private var roadLayer: TiledMapTileLayer = null
  private var grassLayer: TiledMapTileLayer = null

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
    roadLayer = tiledMap.getLayers.get("Tile Layer 1").asInstanceOf[TiledMapTileLayer]
    println(roadLayer)
    grassLayer = tiledMap.getLayers.get("Tile Layer 2").asInstanceOf[TiledMapTileLayer]
    println(grassLayer)

    // Bookmark: Initialize keys
    keyStatus.put(Input.Keys.A, false) // right
    keyStatus.put(Input.Keys.D, false) // left
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    manageHero()
    g.zoom(zoom)

    tiledMapRenderer.setView(g.getCamera)
    tiledMapRenderer.render()

    hero.draw(g)

    g.drawSchoolLogo()
    g.drawFPS()
  }

  def manageHero(): Unit ={
    var goalDirection: String = ""
    var nextPos: Float = 0f

    if (keyStatus.get(Input.Keys.D) || keyStatus.get(Input.Keys.RIGHT)) {
      goalDirection = "RIGHT"
      nextPos = hero.getPosition.x + 1.5f
    }
    if (keyStatus.get(Input.Keys.A) || keyStatus.get(Input.Keys.LEFT)) {
      goalDirection = "LEFT"
      nextPos = hero.getPosition.x - 1.5f
    }
    if (isDrivable(nextPos)) {
      hero.go(goalDirection)
    }
  }

  def isDrivable(nextPos: Float):Boolean ={
    if (nextPos >= getWindowWidth / 2 + 2 * 32 - 8 || (nextPos <= getWindowWidth / 2 - 64 + 8)) {
       false
    }
    else true
  }

  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    keyStatus.put(keycode, false)
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    keyStatus.put(keycode, true)
  }
}