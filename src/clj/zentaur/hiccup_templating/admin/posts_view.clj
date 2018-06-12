(ns blog.hiccup_templating.posts-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn index [content]
  <div class="row">
    <div class="span12">
      <form method="POST" action="/admin/posts">
      {% csrf-field %}
      <p>Title: <br />
        <input class="form-control" type="text" name="title" value="{{title}}" />
      </p>
       {% if errors.title %}
         <div class="alert alert-danger">{{errors.title|join}}</div>
       {% endif %}
       <p>
       <textarea class="form-control" rows="9" cols="50" name="body">{{body}}</textarea>
       </p>
       <div>
         Tags: <br />
         <input type="text" name="tags" value="{{tags}}" />
       </div>
       <div>
         <input type="checkbox" name="active" value="1">Published<br />
         <input type="checkbox" name="discution" value="1">Discussion
       </div>
       {% if errors.body %}
         <div class="alert alert-danger">{{errors.body|join}}</div>
       {% endif %}
       <input type="submit" class="btn btn-primary" value="Save" />
        </form>
    </div>
</div> )


