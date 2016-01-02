create database plocal:/home/steve/demodb
alter database custom useLightweightEdges=true;	
create class Post extends V;
create class Comment extends V;  
create class Has extends E;
create vertex Post set id = '1', content = 'post1'; 
(#11:0)
create vertex Comment set id = '1', content = 'Comment1'; 
(#12:0)
create vertex Comment set id = '2', content = 'Comment2';  
(#12:1)
create vertex Comment set id = '11', content = 'Comment11'; 
(#12:2)   
create vertex Comment set id = '12', content = 'Comment12'; 
(#12:3)
create vertex Comment set id = '121', content = 'Comment121';
(#12:4)
create Edge Has from #11:0 to #12:0;

How can I do a query on #11:0 to get the following JSON tree?

[
  {
    "@RID": "#12:0",
    "id": "1",
    "content": "Comment1",
    "Has": [
      {
        "@RID": "#12:2",
        "id": "11",
        "content": "Comment11"
      },
      {
        "@RID": "#12:3",
        "id": "12",
        "content": "Comment12",
        "Has": [
          {
            "@RID": "#12:4",
            "id": "121",
            "content": "Comment121"
          }
        ]
      }
    ]
  },
  {
    "@RID": "#12:1",
    "id": "2",
    "content": "Comment2"
  }
]


traverse out('Has') from #11:0;

select @RID, id, content, out_Has as children, in_Has as parent from (traverse out('Has') from #11:0) where @class = 'Comment';
