import com.badlogic.gdx.maps.tiled.{TiledMapTile, TiledMapTileLayer, TiledMapTileSet}
import com.badlogic.gdx.math.Vector2
import java.awt.Rectangle

class LayerHelper {
  def extendLayer(layer: TiledMapTileLayer, deltaX: Int, deltaY: Int): TiledMapTileLayer = {
    val newLayer = new TiledMapTileLayer(layer.getWidth + deltaX, layer.getHeight + deltaY, layer.getTileWidth.toInt, layer.getTileHeight.toInt)
    for (x <- 0 until layer.getWidth; y <- 0 until layer.getHeight) {
      newLayer.setCell(x, y, layer.getCell(x, y))
    }
    newLayer
  }

  def getTile(position: Vector2, offsetX: Int, offsetY: Int, tiledLayer: TiledMapTileLayer, tileWidth: Int, tileHeight: Int): TiledMapTile = {
    try {
      val x = (position.x / tileWidth).toInt + offsetX
      val y = (position.y / tileHeight).toInt + offsetY
      tiledLayer.getCell(x, y).getTile
    } catch {
      case _: Exception => null
    }
  }

  def checkOverlap(r1: Rectangle, r2: Rectangle, deltaX: Int = 0,deltaY: Int = 0): Boolean = {
    !(r1.x + r1.width - deltaX < r2.x || r1.y + r1.height - deltaY < r2.y || r1.x + deltaX > r2.x + r2.width || r1.y + deltaY > r2.y + r2.height)
  }

  def getRectangle(x: Int, y: Int, width: Int, height: Int): Rectangle = {
    new Rectangle(x, y, width, height)
  }
}
