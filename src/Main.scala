import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.{Gdx, Input}
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapRenderer, TiledMapTileLayer, TiledMapTileSet, TmxMapLoader, tiles}
import com.badlogic.gdx.math.Vector2

import java.util
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object Main {
  def main(args: Array[String]): Unit = {
    new Main
  }
}

class Main extends PortableApplication(20 * 32, 21 * 32) {
  /*
  Section: Variable list
   */

  // Bookmark: Tiled map managers
  private var tiledMap: TiledMap = null
  private var tiledMapRenderer: TiledMapRenderer = null
  private var natureTiledSet: TiledMapTileSet = null
  private var roadTiledSet: TiledMapTileSet = null
  private var tileLayer: TiledMapTileLayer = null
  private var tileSwitch: Boolean = false

  // Bookmark: Camera manipulation
  private var zoom: Float = 0
  private var cameraPosition = new Vector2(0, 0)

  // Bookmark: Sprite settings
  private var hero: Hero = null
//  private var enemy: Enemy = null
  private var enemies: ArrayBuffer[Enemy] = null
  private var explosion: Explosion = null

  // Bookmark: Key management
  private val keyStatus: util.Map[Integer, Boolean] = new util.TreeMap[Integer, Boolean]

  // Bookmark: Gameplay booleans
  private var isGameOver: Boolean = false

  // Bookmark: Timer
  var timer: Float = 0

  /*
  Section: Initialization
   */

  override def onInit(): Unit = {
    // Bookmark: Set zoom
    zoom = 0.5f

    // Bookmark: Create characters
    hero = new Hero
//    enemy = new Enemy
    enemies = ArrayBuffer[Enemy](new Enemy,new Enemy,new Enemy)
    explosion = new Explosion

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

    // Bookmark: Set game over to false
    isGameOver = false

    Logger.log("New session")
  }

  /*
  Section: Execution
   */

  override def onGraphicRender(g: GdxGraphics): Unit = {
    // Bookmark: cleanup
    g.clear()
    timer += Gdx.graphics.getDeltaTime
    Logger.log(timer.toString)

    // Bookmark: Character managers
    if (!isGameOver) {
      manageHero()
      manageEnemy()
    }
    checkCollision()

    // Bookmark: Camera management
    g.zoom(zoom)
    g.moveCamera(hero.getPosition.x - 160, hero.getPosition.y - 20)

    // Bookmark: Procedural generation
    generate()
    tiledMapRenderer.setView(g.getCamera)
    tiledMapRenderer.render()

    // Bookmark: Drawing all graphical elements
    hero.draw(g)
    for (enemy <- enemies) {
      enemy.draw(g)
    }
    explosion.draw(g)
    if (isGameOver) {
      g.drawString(hero.getPosition.x - 48,hero.getPosition.y + 100,"GAME OVER")
    }
    g.drawSchoolLogo()
    g.drawFPS()

    // Bookmark: Pause if gameOver
  }

  /*
  Section: Character management
   */

  def manageHero(): Unit = {
    var goalDirection: String = ""
    var nextPos: Float = 0f

    // Autodrive
    hero.go("UP")
    hero.setDisplacement(1.5f)

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

  def manageEnemy(): Unit ={
    for (enemy <- enemies) {
      enemy.drive()
    }
  }

  def checkCollision(): Unit ={
    hero.setRectangle(hero.getPosition,16,16 + 15)
    for (enemy <- enemies){
      enemy.setRectangle(enemy.getPosition,16,16 + 15)

      if (hero.getRectangle.overlaps(enemy.getRectangle)) {
        gameover()
      }
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

  /*
 Section: Gameplay events
  */

  def gameover(): Unit = {
    explosion.setPostion(hero.getPosition.x,hero.getPosition.y + 16)
    explosion.show = true
    isGameOver = true

    if (keyStatus.get(Input.Keys.SPACE)) {
      onInit()
    }
  }

  /*
  Section: Procedural generation
   */

  def generate(): Unit = {
    if (hero.getPosition.y + getWindowHeight / 2 >= tileLayer.getHeight * 32) {
      var layer: TiledMapTileLayer = tiledMap.getLayers.get("Tile Layer 1").asInstanceOf[TiledMapTileLayer]
      var layer1: TiledMapTileLayer = tiledMap.getLayers.get("Tile Layer 2").asInstanceOf[TiledMapTileLayer]

      // Bookmark: Rebuild old layer into new
      val newLayer = new TiledMapTileLayer(layer.getWidth, layer.getHeight + 1, 32, 32) // Adds one more row to map
      val newLayer1 = new TiledMapTileLayer(layer1.getWidth, layer1.getHeight + 1, 32, 32)
      for (x <- 0 until layer.getWidth; y <- 0 until layer.getHeight) { // Retrieves all previous map information
        newLayer.setCell(x, y, layer.getCell(x, y))
        newLayer1.setCell(x, y, layer1.getCell(x, y))
      }

      // Bookmark: Delete unnecessary tiles
      for (x <- 0 until layer.getWidth by 32; y <- 0 until hero.getPosition.y.toInt - 64 by 32) {
        newLayer.setCell(x, y, null)
      }

      // Bookmark: Prepare tile type and position to add
      val grassTile: StaticTiledMapTile = new StaticTiledMapTile(natureTiledSet.getTile(417).getTextureRegion)
      val roadTile1: StaticTiledMapTile = new StaticTiledMapTile(roadTiledSet.getTile(51).getTextureRegion)
      val roadTile2: StaticTiledMapTile = new StaticTiledMapTile(roadTiledSet.getTile(52).getTextureRegion)
      var cell: Cell = null

      for (x <- 0 to newLayer.getWidth) {
        if (x == 8 || x == 10) {
          cell = new Cell
          cell.setTile(roadTile1)
          newLayer.setCell(x, newLayer.getHeight - 1, cell)
        }
        else if (x == 9 || x == 11) {
          cell = new Cell
          cell.setTile(roadTile2)
          newLayer.setCell(x, newLayer.getHeight - 1,cell)
        }
        else {
          cell = new Cell
          cell.setTile(grassTile)
          newLayer.setCell(x, newLayer.getHeight - 1, cell)
        }
        vegetationGenerator(newLayer1)
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

  // Bookmark: Generate vegetation to look nice
  def vegetationGenerator(layer: TiledMapTileLayer): Unit = {
    val shrubTile: StaticTiledMapTile = new StaticTiledMapTile(natureTiledSet.getTile(368).getTextureRegion)
    var cell: Cell = null
    val trees: Array[Int] = Array(334,335,320,321)
    var vegeTile: StaticTiledMapTile = null

    if (!tileSwitch) tileSwitch = true
    else tileSwitch = false

    for (x <- 0 to layer.getWidth) {
      if (x == 7 || x == 12) {
        cell = new Cell
        cell.setTile(shrubTile)
        layer.setCell(x,layer.getHeight -1,cell)
      }
      else if (tileSwitch && (x == 6 || x == 13)) {
        val ID = Random.shuffle(trees).head
        cell = new Cell
        vegeTile = new StaticTiledMapTile(natureTiledSet.getTile(ID).getTextureRegion)
        cell.setTile(vegeTile)
        layer.setCell(x,layer.getHeight - 1,cell)
      }
      else {
        layer.setCell(x,layer.getHeight - 1,null)
      }
    }
  }


  /*
  Section: Finishing off
   */

  override def onDispose(): Unit = {
    super.onDispose()
    Logger.log("Bye bye")
  }
}