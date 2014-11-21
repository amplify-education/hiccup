## Overview

Hiccup is an Android library that layers _Http In ContentProviders_.

It aims to bring RESTful and MVC-like concepts to ContentProviders to separate client/server concerns,
allow UI/data layers to evolve independently, and improve code maintainability. A ContentProvider is like
a locally running server, so the idea is that we can treat it like one and borrow some best practices from
the web.

#### Status
Alpha, work in progress. Use at your own discretion. Currently supports:

1. GET requests
1. POST requests

#### Motivation

Android's ContentProvider is a great tool for data abstraction and sharing,
but its interface is confusing and has several limitations. Mainly, its
interface is half REST and half SQL, which result in the following:

1. clients (via ContentResolver) are forced to know underlying db schema via sql projections, where clauses, etc.
1. as a result, restructuring tables (data normalization) breaks client code
1. table joins are difficult to support when using uri's + sql
1. difficult to represent complex models in flat maps (Cursor or ContentValues)
1. assumes SQL backend (ie, does not easily support NOSQL, file, shared pref, etc.)

Hiccup tries to overcome these challenges.

## Usage

#### Client side (CursorAdapter)
In this example, let's assume we have used a CursorLoader that made a RESTful request to ``content://com.your.authority/categories/52/products?sort=name``.
We get back a Cursor, which has an **_id** (for adapters) and **body**, which in this example
is JSON of our domain models. Now we can bind our views simply using those domain models.

```Java
@Override
public void bindView(View view, Context context, Cursor cursor) {
    int bodyIndex = cursor.getColumnIndex("body");
    String body = cursor.getString(bodyIndex);
    Product product = myJsonParser.fromJson(body, Product.class);

    TextView productNameView = (TextView) view.findViewById(R.id.product_name);
    productNameView.setText(product.name());
    productPriceView.setText(product.price());
}
```

#### Server side (ContentProvider)

Here, we init Hiccup service, create routes, and delegate to controllers.

```Java
@Override
public boolean onCreate() {
    super.onCreate();
    // ContentAdapters let us define how domain models are converted into a response.
    ContentAdapter contentAdapter = new HttpContentAdapter(myJsonParser);
    hiccupService = new HiccupService("com.your.authority")
            .newRoute("categories/#/products", new ProductsCollectionController(contentAdapter));
}

@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                    String sortOrder) {
    // Delegate GET request to controller based on route
    return hiccupService.delegateQuery(uri);
}
```

#### Server side (Controller)

This controller is responsible for the products collection for the route: _categories/#/products_.

```Java
public class ProductsCollectionController extends AbstractController<Product> {

    public ProductsCollectionController(ContentAdapter<Product> contentAdapter) {
        super(contentAdapter, Product.class);
        // set some other variables...
    }

    @Override
    public Iterable<Product> handleGet(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String categoryId = uri.getPathSegments().get(1);
        String sort = uri.getQueryParameter("sort");
        Cursor productsCursor = findProductsByCategory(db, categoryId, sort);

        // Convert our SQLiteCursor to list of domain models/POJOs
        return convertCursorToProducts(productsCursor);
    }

    private Cursor findProductsByCategory(SQLiteDatabase db, String categoryId, String sort) {
        return db.query(
            // JOIN across multiple tables and sort
        );
    }
}
```
