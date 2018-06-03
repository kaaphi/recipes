FROM openjdk:9-slim

WORKDIR /recipe

ADD ./build/distributions/recipe-app-1.0.tar /recipe

EXPOSE 7000

     
CMD ["./recipe-app-1.0/bin/recipe-app"]