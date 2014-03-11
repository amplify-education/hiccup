## Overview

Hiccup is an Android library that layers _Http In ContentProviders_.

It aims to bring RESTful and MVC-like concepts to ContentProviders to separate client/server concerns, simplify app
client access, and improve code maintainability. A ContentProvider is essentially a server, so the idea is that we
can treat it like one and use some well-established best practices.

#### Status
Alpha, work in progress. Use at your own discretion. Currently supports:

1. GET requests
2. POST requests

#### Motivation

The default Android ContentProvider interface poses some challenges & limitations. It is half REST and half SQL,
which result in the following:

1. exposes underlying db representation to clients
1. table joins are difficult to support when using uri's
1. difficult to represent complex models in flat map of Cursor results or ContentValues
1. assumes SQL backend (ie, does not easily support NOSQL, file, shared pref, etc.)

Hiccup tries to overcome these challenges.

## Usage

#### Step 1. Example CursorAdapter
In this example, let's assume we have used a CursorLoader to make a REST request to ``content://com.your.authority/categories/52/products?sort=name``.
We get back an HttpCursor, which has an **_id** (for adapters) and **body**, which in this example is JSON of our domain models.
Now we can bind our views using those domain models.

```Java
@Override
public void bindView(View view, Context context, Cursor cursor) {
    int bodyIndex = cursor.getColumnIndex("body");
    String body = cursor.getString(bodyIndex);
    Product product = new Gson().fromJson(body, Product.class);

    TextView productNameView = (TextView) view.findViewById(R.id.product_name);
    productNameView.setText(playlist.name());
}
```

#### Step 2. Example ContentProvider/Routes

Here, we init Hiccup service, create routes, and delegate to controllers.

```Java
@Override
public boolean onCreate() {
    super.onCreate();
    hiccupService = new HiccupService("com.your.authority")
            .newRoute("categories/#/products", new ProductsCollectionController());
}

@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                    String sortOrder) {
    // Delegate GET request to controller based on route
    return hiccupService.delegateQuery(uri);
}
```

#### Step 3. Example Controller

This controller is responsible for the products collection for the route: _categories/#/products_.

```Java
public class ProductsCollectionController implements Controller {
    // constructor, etc...
    @Override
    public Response<Product> get(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String categoryId = uri.getPathSegments().get(1);
        String sort = uri.getQueryParameter("sort");
        Cursor productsCursor = findProductsByCategory(db, categoryId, sort);

        // Convert our SQLiteCursor to list of domain models/POJOs
        final List<Product> products = convertCursorToProducts(productsCursor);

        // Return a Response, which lets us define how the body of each result model
        // is generated in the HttpCursor that Hiccup will return from this query
        return new Response<Product>() {
            @Override
            public Iterable<Playlist> getResults() {
                return products;
            }
            @Override
            public String getBody(Playlist model) {
                return jsonConverter.toJson(model);
            }
        };
    }

    private Cursor findProductsByCategory(SQLiteDatabase db, String categoryId, String sort) {
        return db.query(
            // JOIN across multiple tables and sort
        );
    }
}
```

