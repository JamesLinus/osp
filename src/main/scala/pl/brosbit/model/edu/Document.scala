package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._

//import org.bson.types.ObjectId

import java.util.Date
import org.bson.types.ObjectId


object Document extends MongoDocumentMeta[Document] {
  override def collectionName = "documents"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Document(ObjectId.get, "", "", "", "", 0L, "", 0L, "", 0)
}

case class Document(var _id: ObjectId, var title: String, var descript: String, var department: String,
                    var content: String, var authorId: Long, var authorName: String, var subjectId: Long,
                    var subcjectName: String, var lev: Int) extends MongoDocument[Document] {
  def meta = Document
}
