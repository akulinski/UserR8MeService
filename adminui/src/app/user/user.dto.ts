declare module namespace {

  export interface Rate {
    rate: number;
    sender?: any;
    question: string;
  }

  export interface RatesMap {
  }

  export interface Comment {
    comment: string;
    commenter?: any;
  }

  export interface Authority {
    authorityType: string;
    authority: string;
  }

  export interface UserDto {
    id: string;
    username: string;
    email: string;
    created?: any;
    modificationDate: Date;
    link?: any;
    isEnabled: boolean;
    rates: Rate[];
    ratesMap: RatesMap;
    currentRating: number;
    comments: Comment[];
    authorities: Authority[];
    enabled: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    credentialsNonExpired: boolean;
    new: boolean;
  }

}

