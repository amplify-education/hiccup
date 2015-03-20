
## Overview

Hiccup is a RESTful approach to ContentProviders using HTTP semantics.

The idea is to encourage designing ContentProviders like web services. They are so similar that we can benefit from some
established best practices, including RESTful interfaces and MVC-like concepts. It *does not* make actual HTTP requests
to the internet.

Some benefits to this approach include allowing any persistence implementation in ContentProviders (eg, sqlite, shared
prefs, ORMs) and permitting versioned endpoints/resources for cross-process data sharing.

#### Status
[![Build Status](https://travis-ci.org/amplify-education/hiccup.svg?branch=master)](https://travis-ci.org/amplify-education/hiccup)

Approaching a 1.0 pre-release. Grab **1.0 snapshot version** like so:
```xml
<dependencies>
  <dependency>
    <groupId>com.amplify</groupId>
    <artifactId>hiccup</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Currently supports: _GET_, _POST_, _PUT_, _DELETE_, and batch requests

#### Goals

1. improve code maintainability of ContentProviders
1. separate client/provider concerns
1. allow UI/data layers to evolve independently
1. expose a clean & simple api for developers
1. be super lightweight
1. remain compatible within normal ContentProvider behavior

#### Motivation

Android's ContentProvider is a great tool for data access and cross-app sharing,
but its interface is confusing and has several limitations. Mainly, its
interface is half REST and half SQL, which result in the following:

1. clients (eg, Activities) are forced to know underlying db schema via sql projections, where clauses, etc.
1. restructuring tables (data normalization) breaks client code
1. db transactions, joins, cascades, etc. are difficult to support when using uri's + sql
1. data integrity is difficult to maintain when it's left up to clients
1. assumes sqlite backend (ie, does not easily support in-memory, file, shared pref, NoSQL, etc.)
1. data/resources cannot be versioned

## Usage

#### Client (eg, Activity) makes GET/POST/PUT/DELETE requests

```Java
@Override
public void onCreate() {
    super.onCreate();
    setContentView(R.layout.whatever);

    findViewById(R.id.delete_button).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.your.authority/posts/" + postId);
                hiccupClient.delete(uri); // on UI thread just for demo purposes
            }
    });
}
```

#### ContentProvider dispatches requests to controllers

```Java
private HiccupService hiccupService;

@Override
public boolean onCreate() {
    super.onCreate();
    SQLiteOpenHelper dbHelper = createDbHelper();
    hiccupService = new HiccupService("com.your.authority")
            .newRoute("posts/#", new PostsController(dbHelper))
            .newRoute("posts/#/comments", new CommentsController(dbHelper))
            // etc
            ;
}

@Override
public int delete(Uri uri, String selection, String[] selectionArgs) {
    // Delegate to Controller based on incoming uri and registered #newRoute()'s
    // (intentionally ignores the selection+args)
    return hiccupService.delegateDelete(uri);
}
```

#### Controller handles transactions, data integrity, etc.

```Java
public class PostsController implements Controller {
    // constructor, etc...

    @Override
    public int delete(Uri uri) {
        String postId = uri.getPathSegments().get(1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("comments", "post_id = ?", new String[]{postId});
            db.delete("posts", "_id = ?", new String[]{postId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Uri changedUri = Uri.parse("content://com.your.authority/posts");
        contentResolver.notifyChange(changedUri, null);
    }
}
```

# Thanks!

To [Dave Cameron](https://github.com/davcamer), a former colleague, who inspired some ideas here. Please be sure to
check oot his profile, eh?
