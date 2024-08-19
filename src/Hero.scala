import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.Vector2

class Hero extends DrawableObject {
  // Bookmark: Direction enumeration
  object Direction extends Enumeration {
    type Direction = Value
    val UP, DOWN, RIGHT, LEFT, NULL = Value
  }

  // Bookmark: Image settings
  private val SPRITE_WIDTH = 16
  private val SPRITE_HEIGHT = 16
  private val carBitmap: BitmapImage = new BitmapImage("data/Res/Characters/RedStrip.png")

  // Bookmark: Position settings
  private val initialPosition: Vector2 = new Vector2(335,200)
  private var position: Vector2 = initialPosition

  def getPosition: Vector2 = position

  def setPosition(newPos: Vector2): Unit = {

  }

  override def draw(g: GdxGraphics): Unit = {
    g.drawTransformedPicture(position.x,position.y,90f,SPRITE_HEIGHT,SPRITE_WIDTH,carBitmap)
  }
}
