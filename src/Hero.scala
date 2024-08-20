import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.Vector2

class Hero extends DrawableObject {
  // Bookmark: Map information
  private val windowWidth = 20 * 32

  // Bookmark: Image settings
  private val SPRITE_WIDTH = 16
  private val SPRITE_HEIGHT = 16
  private val carBitmap: BitmapImage = new BitmapImage("data/Res/Characters/RedStrip.png")

  // Bookmark: Position settings
  private val initialPosition: Vector2 = new Vector2(335,100)
  private var position: Vector2 = initialPosition
  private var move: Boolean = false

  def getPosition: Vector2 = position

  def setPosition(newPos: Vector2): Unit = {
    position = newPos
  }

  def go(direction: String): Unit = {
    direction match {
      case "RIGHT" => position.add(1.5f, 0)
      case "LEFT" => position.add(-1.5f, 0)
      // Todo: to delete
      case "UP" => position.add(0,1.5f)
      case _ => position.add(0, 0)
    }
  }
  override def draw(g: GdxGraphics): Unit = {
    g.drawTransformedPicture(position.x,position.y,90f,SPRITE_HEIGHT,SPRITE_WIDTH,carBitmap)
  }
}
