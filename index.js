const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const reference = admin.database();


//Sends notifications when a new comment is added 
exports.commentNotification = functions.database.ref('/Reviews/{storyId}/{pushId}')
.onCreate((snapshot, context)=>{
        const review = snapshot.val();
        const author = review.author;
        const msg = review.message;
        var newsTitle;

        return reference.ref('News').child(context.params.pushId).child('newsTitle').once('value')
      .then(snap => {
        newsTitle = snap.val()
        var payload = {
             notification: {
              title: author + " commented on " + snap.val(),
              body: msg,
              sound: "default"
            }
          };

          const options = {
             priority: "high",
             timeToLive: 604800
         };

          return admin.messaging().sendToTopic("all",payload,options)
            .then(function (response){
              console.log("Message sent...! " + newsTitle)
              return;
            }).catch(function (error){
              console.log("Failed ",error);
              return;
          });
      }).catch(reason => {
          console.log("Story title not found ",reason);
          return;
      });
});


//Sends notifications when a new comment is added 
exports.commentNotification = functions.database.ref('/Comments/{storyId}/{pushId}')
.onCreate((snapshot, context)=>{
        const comment = snapshot.val();
        const author = comment.author;
        const msg = comment.message;
        var newsTitle;

        return reference.ref('News').child(context.params.pushId).child('newsTitle').once('value')
      .then(snap => {
        newsTitle = snap.val()
        var payload = {
             notification: {
              title: author + " commented on " + snap.val(),
              body: msg,
              sound: "default"
            }
          };

          const options = {
             priority: "high",
             timeToLive: 604800
         };

          return admin.messaging().sendToTopic("all",payload,options)
            .then(function (response){
              console.log("Message sent...! " + newsTitle)
              return;
            }).catch(function (error){
              console.log("Failed ",error);
              return;
          });
      }).catch(reason => {
          console.log("Story title not found ",reason);
          return;
      });
});


//Send notification when a News item is posted
exports.onNewsPosted = functions.database.ref('/News/{pushId}')
    .onCreate((snapshot, context) =>{
          const news = snapshot.val();
            const newsTitle = news.newsTitle;
            var payload = {
               notification: {
                title: "News",
                body:  newsTitle,
                sound: "default"
              }
           };

          const options = {
               priority: "high",
               timeToLive: 604800
           };

          return admin.messaging().sendToTopic("all",payload,options)
            .then(function (response){
              console.log("Message sent...! ")
              return;
            })
          .catch(function (error){
              console.log("Failed ",error);
              return;
          });
  });


//Sends notifications when a new question is posted
exports.onQuestionPosted = functions.database.ref('/Questions/{pushId}')
  .onCreate((snapshot, context) =>{
        //Added question
          const question = snapshot.val();

            var payload = {
                notification: {
                title: "New question on " + crop,
                body: question.userUrl + " asked a question about " + question.problemType + " affecting " + question.crop,
                sound: "default"
              }
            };

            const options = {
                priority: "high",
                timeToLive: 604800
            };

            return admin.messaging().sendToTopic("all",payload,options)
                .then(function (response){
                console.log("Message sent...! ")
                return;
                })
            .catch(function (error){
                console.log("Failed ",error);
                return;
            });  
     
});


//Sends notifications when a new question is answered
exports.onQuestionAnswered = functions.database.ref('/Answers/{answerId}/{pushId}')
  .onWrite((snapshot, context) =>{
      if (event.data.previous.numChildren() < event.data.numChildren()) {
        //Added question
          const question = snapshot.val();
          const statement = question.question;
          var payload = {
              notification: {
                title: "New response on question",
                body: statement + " responded to a question about " + uestion.crop,
                sound: "default",
                click_action: "com.techart.atszambia.QuestionsActivity"
              }
          };

          const options = {
              priority: "high",
              timeToLive: 604800
          };

          return admin.messaging().sendToTopic("all",payload,options)
              .then(function (response){
              console.log("Message sent...! ")
              return;
              })
          .catch(function (error){
              console.log("Failed ",error);
              return;
          });
  
      } else if (event.data.previous.numChildren() > event.data.numChildren()) {
        //Deleted chapter
        return null;
      }  else {
            return null;
        }
});

