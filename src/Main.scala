import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTileLayer, TiledMapTileSet, TmxMapLoader}
import com.badlogic.gdx.math.Vector2

import java.util
import java.util.{Map, TreeMap}
import scala.annotation.meta.param

object Main {
  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication(20 * 32, 21 * 32) {
  // Bookmark: Tiled map managers
  private var tiledMap: TiledMap = null
  private var tiledMapRenderer: TiledMapRenderer = null
  private var natureTiledSet: TiledMapTileSet = null
  private var roadTiledSet: TiledMapTileSet = null
  private var tileLayer: TiledMapTileLayer = null

  // Bookmark: Camera manipulation
  private var zoom: Float = 0
  private var cameraPosition = new Vector2(0, 0)

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
    natureTiledSet = tiledMap.getTileSets.getTileSet("TopDownTileset")
    roadTiledSet = tiledMap.getTileSets.getTileSet("City Prop Tileset update 2")

    // Bookmark: Initialize keys
    keyStatus.put(Input.Keys.A, false) // right
    keyStatus.put(Input.Keys.D, false) // left

    // Bookmark: Layer references
    tileLayer = tiledMap.getLayers.get("Tile Layer 1").asInstanceOf[TiledMapTileLayer]
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    manageHero()

    g.zoom(zoom)
    g.moveCamera(hero.getPosition.x - 150, hero.getPosition.y - 20)
    generate()

    tiledMapRenderer.setView(g.getCamera)
    tiledMapRenderer.render()

    hero.draw(g)

    g.drawSchoolLogo()
    g.drawFPS()
  }

  def manageHero(): Unit = {
    var goalDirection: String = ""
    var nextPos: Float = 0f

    // Autodrive
    hero.go("UP")

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

  def isDrivable(nextPos: Float): Boolean = {
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

  def generate(): Unit = {
    println("hello")
    var layer: TiledMapTileLayer = tiledMap.getLayers.get("Tile Layer 1").asInstanceOf[TiledMapTileLayer]
    var layer1: TiledMapTileLayer = tiledMap.getLayers.get("Tile Layer 2").asInstanceOf[TiledMapTileLayer]
    println(layer.getHeight)

    // Bookmark: Rebuild old layer into new
    val newLayer = new TiledMapTileLayer(layer.getWidth, layer.getHeight + 1, 32, 32) // Adds one more row to map
    val newLayer1 = new TiledMapTileLayer(layer1.getWidth, layer1.getHeight + 1, 32, 32)
    for (x <- 0 until layer.getWidth; y <- 0 until layer.getHeight) { // Retrieves all previous map information
      newLayer.setCell(x, y, layer.getCell(x, y))
      newLayer1.setCell(x, y, layer1.getCell(x, y))
    }

    // Bookmark: Prepare tile type and position to add
    val grassTile: StaticTiledMapTile = new StaticTiledMapTile(natureTiledSet.getTile(417).getTextureRegion)
    val roadTile: StaticTiledMapTile = new StaticTiledMapTile(roadTiledSet.getTile(51).getTextureRegion)

    val cell: Cell = new Cell
    for (x <- 0 to newLayer.getWidth) {
      if (x < getWindowWidth / 2 + 2 * 32 - 8 && (x > getWindowWidth / 2 - 64 + 8)){
        cell.setTile(roadTile)
      }
      else {
        cell.setTile(grassTile)
      }
      newLayer.setCell(x, newLayer.getHeight - 1, cell)
    }


    // Bookmark: Update layer
    newLayer.setName("Tile Layer 1")
    newLayer1.setName("Tile Layer 2")
    tiledMap.getLayers.remove(layer)
    tiledMap.getLayers.remove(layer1)
    tiledMap.getLayers.add(newLayer)
    tiledMap.getLayers.add(newLayer1)
    tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap)
  }
}