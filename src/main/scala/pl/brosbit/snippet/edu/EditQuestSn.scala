package pl.brosbit.snippet.edu

import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import edu._
import Helpers._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._

class EditQuestSn extends BaseResourceSn {
    
   val subjectId = subjectNow.id
        
    
    def subjectChoice() = {
        super.subjectChoice("/educontent/questions")
    }
    
    def showQuests() = {
        val userId = user.id.is
        
        "tr" #> QuizQuestion.findAll(("authorId"->userId)~("subjectId"->subjectId)).map(quest => {
            <tr id={quest._id.toString}><td>{Unparsed(quest.question)}</td>
            <td>{quest.answers.map(f => <span class="good">{f}</span>)}</td><td>{quest.fake.map(f => <span class="wrong">{f}</span>)}</td>
            <td>{levMap(quest.lev.toString)}</td>
            <td>{quest.dificult}</td><td>{quest.department}</td>
            <td><button class="btn btn-success" onclick="editQuest.editQuestion(this);">
            	<span class="glyphicon glyphicon-edit"></span></button></td></tr>
        })
    }
    
    //working ....
    def editQuest() = {
        var id = ""
        var question = ""
        var level = ""
        var answer = ""
        var wrongAnswers =""
        var subject = ""
        var dificult = "2"
        var department = ""
                
        def save():JsCmd = {
            
            val userId = user.id.is
            val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
            if(quest.authorId != 0L && quest.authorId != userId) return Alert("To nie twoje pytanie!")
            quest.authorId = userId
            quest.answers = answer.split(";").toList.map(a => a.trim).filterNot(a => a.length() == 0)
            quest.fake = wrongAnswers.split(";").toList.map(a => a.trim).filterNot(a => a.length() == 0)
            quest.question = question
            quest.subjectId = subjectNow.id
            quest.subjectName = subjectNow.name
            quest.department = department
            quest.dificult = tryo(dificult.toInt).openOr(9)
            quest.lev = tryo(level.toInt).openOr(1)
            quest.save
            JsFunc("editQuest.insertQuestion",quest._id.toString).cmd
        }
        
        def delete():JsCmd = {
             println("+++++++++++++++++++ Del QUEST ")
            val userId = user.id.is 
            QuizQuestion.find(id) match {
                case Some(quest) => {
                    if(quest.authorId == userId) {
                        quest.delete
                        JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
                    }
                    else Alert("To nie twoje pytanie!")
                }
                case _ => Alert("Nie znaleziono pytania!")
            }
        }

        val dificults = 1 to 9 map( i => {val iS = i.toString;(iS, iS)})
        val departments = subjectNow.departments.map(d => (d, d))
        
       val form = "#idQuest" #> SHtml.text(id, id = _) &
       "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
       "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
       "#subjectQuest" #> SHtml.text(subjectNow.name, x => Unit, "readonly"-> "readOonly") &
       "#levelQuest" #> SHtml.select(levList, Full(subjectNow.lev.toString), level = _) &
       "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
       "#departmentQuest" #> SHtml.select(departments, Full(subjectNow.departments.head), x => department = x.trim) &
       "#dificultQuest" #> SHtml.select(dificults, Full(dificult), dificult = _) &
       "#saveQuest" #> SHtml.ajaxSubmit("Zapisz",save) &
       "#deleteQuest" #> SHtml.ajaxSubmit("Usuń",delete) andThen SHtml.makeFormsAjax
       
        "form" #> (in => form(in))
       
    }
 
    
    //private def printParam = println("subjectId="+ subjectId + " level=" + level)

}