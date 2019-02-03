// CalendarManager.m
#import "IoTHubDeviceModule.h"
#import <React/RCTLog.h>
#import <React/RCTConvert.h>

@implementation IoTHubDeviceModule

// To export a module named after this Class
RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents
{
  return @[@"onDesiredPropertyUpdate"];
}

RCT_EXPORT_METHOD(connectToHub:
                  (NSString *)connectionString
                  :(NSArray *)desiredPropertySubscriptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject){

  //Emit some events back to React Native to mock desired property update events
  [self sendEventWithName:@"onDesiredPropertyUpdate" body:@{@"propertyJson": @"{\"property\":{\"key\": \"tellMe\", \"value\": \"who's a good device?\"}}"}];
  [self sendEventWithName:@"onDesiredPropertyUpdate" body:@{@"propertyJson": @"{\"property\":{\"key\": \"tellMe\", \"value\": \"are you a good device?\"}}"}];
  [self sendEventWithName:@"onDesiredPropertyUpdate" body:@{@"propertyJson": @"{\"property\":{\"key\": \"tellMe\", \"value\": \"yes you are!\"}}"}];

  //TODO: Connect to IoT Hub

  //TODO: Subscribe to desired properties
  RCTLogInfo(@"obj-c Subscribe to these reported properties: %@", desiredPropertySubscriptions);

  //TODO: As desired properties come in, emit onDesiredPropertyUpdate event (these things above)



  //The rest of this is placeholder for testing - it can all be removed
  @try{
    RCTLogInfo(@"obj-c Connect to IoT Hub as device using this connection string: %@", connectionString);
    resolve([NSString stringWithFormat:@"Sucess! %@", connectionString]);
  }@catch (NSError *error){
    reject(NSStringFromClass([self class]),
           [NSString stringWithFormat:@"Error: %@: %ld", [error domain], [error code]],
           error);
  }@catch (NSException *exception){
    RCTLogError(@"%@", [exception callStackSymbols]);

    NSMutableDictionary * info = [NSMutableDictionary dictionary];
    [info setValue:exception.name forKey:@"ExceptionName"];
    [info setValue:exception.reason forKey:@"ExceptionReason"];
    [info setValue:exception.callStackReturnAddresses forKey:@"ExceptionCallStackReturnAddresses"];
    [info setValue:exception.callStackSymbols forKey:@"ExceptionCallStackSymbols"];
    [info setValue:exception.userInfo forKey:@"ExceptionUserInfo"];

    NSError *error = [[NSError alloc] initWithDomain:@"hello" code:1 userInfo:info];

    reject(NSStringFromClass([self class]),
           [NSString stringWithFormat:@"Exception: %@: %@", [exception name], [exception reason]],
           error);
  }@finally {
    //clean up
  }

}

#define IS_OBJECT(T) _Generic( (T), id: YES, default: NO)


RCT_EXPORT_METHOD(reportProperties:
                  (NSDictionary *)properties
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject){
  @try{
    RCTLogInfo(@"Sending Properties...");
    for(NSString *key in [properties allKeys]) {
      if([[properties objectForKey:key] isKindOfClass:[NSString class]]){
        RCTLogInfo(@"obj-c String Property: %@: %@", key, [properties objectForKey:key]);
      }else if([[properties objectForKey:key] isKindOfClass:[NSNumber class]]){
        if([self isBoolNumber:[properties objectForKey:key]]){
          RCTLogInfo(@"obj-c Boolean Property: %@: %@", key, [properties objectForKey:key]);
        }else{
          RCTLogInfo(@"obj-c Number Property: %@: %@", key, [properties objectForKey:key]);
        }
      } else {
        RCTLogInfo(@"obj-c Unknown Property: %@: %@", key, [properties objectForKey:key]);
      }
    }
    resolve(properties);
  }@catch (NSError *error){
    reject(NSStringFromClass([self class]),
           [NSString stringWithFormat:@"Error: %@: %ld", [error domain], [error code]],
           error);
  }@catch (NSException *exception){
    RCTLogError(@"%@", [exception callStackSymbols]);

    NSMutableDictionary * info = [NSMutableDictionary dictionary];
    [info setValue:exception.name forKey:@"ExceptionName"];
    [info setValue:exception.reason forKey:@"ExceptionReason"];
    [info setValue:exception.callStackReturnAddresses forKey:@"ExceptionCallStackReturnAddresses"];
    [info setValue:exception.callStackSymbols forKey:@"ExceptionCallStackSymbols"];
    [info setValue:exception.userInfo forKey:@"ExceptionUserInfo"];

    NSError *error = [[NSError alloc] initWithDomain:@"hello" code:1 userInfo:info];

    reject(NSStringFromClass([self class]),
           [NSString stringWithFormat:@"Exception: %@: %@", [exception name], [exception reason]],
           error);
  }@finally {
    //clean up
  }
}


- (BOOL) isBoolNumber:(NSNumber *)num
{
  CFTypeID boolID = CFBooleanGetTypeID(); // the type ID of CFBoolean
  CFTypeID numID = CFGetTypeID((__bridge CFTypeRef)(num)); // the type ID of num
  return numID == boolID;
}

@end
